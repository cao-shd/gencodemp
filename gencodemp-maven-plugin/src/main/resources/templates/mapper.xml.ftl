<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${package.Mapper}.${table.mapperName}">

<#if enableCache>
    <!-- 开启二级缓存 -->
    <cache type="${cacheClassName}"/>
</#if>
<#if baseResultMap>
    <!-- 通用查询映射结果 -->
    <resultMap id="BaseResultMap" type="${package.Parent}.${poPkg}.${entity}${poSuffix}">
<#list table.commonFields as field>
<#--生成主键排在第一位-->
<#if field.keyFlag>
        <id column="${field.name}" property="${field.propertyName}" />
</#if>
</#list>
<#list table.fields as field>
<#--生成普通字段 -->
<#if !field.keyFlag>
        <result column="${field.name}" property="${field.propertyName}" />
</#if>
</#list>
<#--生成公共字段 -->
<#list table.commonFields as field>
<#if !field.keyFlag>
        <result column="${field.name}" property="${field.propertyName}" />
</#if>
</#list>
    </resultMap>

</#if>
<#if baseColumnList>
    <!-- 通用查询结果列 -->
    <sql id="BaseColumns">
<#list table.commonFields as field>
<#if field.keyFlag>
        ${field.name},
</#if>
</#list>
<#list table.fields as field>
<#if !field.keyFlag>
        ${field.name},
</#if>
</#list>
<#list table.commonFields as field>
<#if !field.keyFlag>
        ${field.columnName}<#if field_has_next>,</#if>
</#if>
</#list>
    </sql>

</#if>
</mapper>
