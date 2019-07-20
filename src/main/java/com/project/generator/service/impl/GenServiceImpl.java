package com.project.generator.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.common.config.Global;
import com.project.common.constant.Constants;
import com.project.common.utils.StringUtils;
import com.project.generator.domain.ColumnInfo;
import com.project.generator.domain.TableInfo;
import com.project.generator.mapper.GenMapper;
import com.project.generator.service.IGenService;
import com.project.generator.util.GenUtils;
import com.project.generator.util.VelocityInitializer;

/**
 * 代码生成 服务层处理
 * 
 * @author lws
 */
@Service
public class GenServiceImpl implements IGenService
{
    private static final Logger log = LoggerFactory.getLogger(GenServiceImpl.class);

    @Autowired
    private GenMapper genMapper;

    /**
     * 查询ry数据库表信息
     * 
     * @param tableInfo 表信息
     * @return 数据库表列表
     */
    @Override
    public List<TableInfo> selectTableList(TableInfo tableInfo)
    {
        return genMapper.selectTableList(tableInfo);
    }

    /**
     * 生成代码
     * 
     * @param tableName 表名称
     * @return 数据
     */
    @Override
    public byte[] generatorCode(String tableName)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        // 查询表信息
        TableInfo table = genMapper.selectTableByName(tableName);
        // 查询列信息
        List<ColumnInfo> columns = genMapper.selectTableColumnsByName(tableName);
        // 生成代码
        generatorCode(table, columns, zip);
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 批量生成代码
     * 
     * @param tableNames 表数组
     * @return 数据
     */
    @Override
    public byte[] generatorCode(String[] tableNames)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableName : tableNames)
        {
            // 查询表信息
            TableInfo table = genMapper.selectTableByName(tableName);
            // 查询列信息
            List<ColumnInfo> columns = genMapper.selectTableColumnsByName(tableName);
            // 生成代码
            generatorCode(table, columns, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    @Override
    public byte[] generatorCodeByDict(String[] tableNames) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableName : tableNames)
        {
            // 查询表信息
            List<List<Map<String, Object>>> resultList = new ArrayList<>();
            Map<String, Integer> dictGroupMap = new HashMap<>();
            List<Map<String,Object>> list = genMapper.selectDictList();
            for (Map<String, Object> map : list) {
                String dict_group = map.get("dict_group").toString();
                String dict_group_upper = dict_group.toUpperCase();
                map.put("dict_group_upper", dict_group_upper);
                if (!dictGroupMap.containsKey(dict_group)) {
                    List<Map<String, Object>> dictList = new ArrayList<>();
                    dictList.add(map);
                    resultList.add(dictList);
                    dictGroupMap.put(dict_group, (resultList.size() -1));
                }else {
                    List<Map<String, Object>> dictList = resultList.get(dictGroupMap.get(dict_group));
                    dictList.add(map);
                }
            }

//            // 查询列信息
//            List<ColumnInfo> columns = genMapper.selectTableColumnsByName(tableName);
//            // 生成代码
            generatorCodeDict(resultList, zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 生成代码
     */
    public void generatorCode(TableInfo table, List<ColumnInfo> columns, ZipOutputStream zip)
    {
        // 表名转换成Java属性名
        String className = GenUtils.tableToJava(table.getTableName());
        String[] aliasNames = table.getTableName().split("_");
        String aliasName = "";
        if(aliasNames != null && aliasNames.length > 0) {
        	for(int i=0;i<aliasNames.length;i++) {
            	if(i > 0) {
            		aliasName +=aliasNames[i].substring(0,1);
            	}
            }
        }
        if(StringUtils.isEmpty(aliasName)) {
        	aliasName = table.getTableName().substring(0,1);
        }
        
        table.setAliasName(aliasName);
        table.setClassName(className);
        table.setClassname(StringUtils.uncapitalize(className));
        // 列信息
        table.setColumns(GenUtils.transColums(columns));
        // 设置主键
        table.setPrimaryKey(table.getColumnsLast());

        VelocityInitializer.initVelocity();

        String packageName = Global.getPackageName();
        String moduleName = GenUtils.getModuleName(packageName);

        VelocityContext context = GenUtils.getVelocityContext(table);

        // 获取模板列表
        List<String> templates = GenUtils.getTemplates();
        for (String template : templates)
        {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            try
            {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(GenUtils.getFileName(template, table, moduleName)));
                IOUtils.write(sw.toString(), zip, Constants.UTF8);
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            }
            catch (IOException e)
            {
                log.error("渲染模板失败，表名：" + table.getTableName(), e);
            }
        }
    }

    /**
     * 生成代码
     */
    public void generatorCodeDict(List<List<Map<String, Object>>> resultList, ZipOutputStream zip)
    {
        VelocityInitializer.initVelocity();

        String packageName = Global.getPackageName();
        String moduleName = GenUtils.getModuleName(packageName);
        VelocityContext context = new VelocityContext();
        context.put("resultList", resultList);
        // 获取模板列表
        List<String> templates = new ArrayList<String>();
        templates.add("templates/dict/DictConst.java.vm");
        for (String template : templates)
        {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, Constants.UTF8);
            tpl.merge(context, sw);
            try
            {
                String path = "main/java/cn/stylefeng/guns/core/common/constant" + "/DictConst.java";
                // 添加到zip
                zip.putNextEntry(new ZipEntry(path));
                IOUtils.write(sw.toString(), zip, Constants.UTF8);
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            }
            catch (IOException e)
            {
                log.error("渲染模板失败", e);
            }
        }
    }
}
