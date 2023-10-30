package ${package.Mapper};

import ${package.Parent}.${poPkg}.${entity}${poSuffix};
import ${superMapperClassPackage};
<#if mapperAnnotationClass??>
import ${mapperAnnotationClass.name};
</#if>

/**
 * ${table.comment!} Mapper 接口
 *
 * @author ${author}
 * @since ${date}
 */
<#if mapperAnnotationClass??>
@${mapperAnnotationClass.simpleName}
</#if>
<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}${poSuffix}>
<#else>
public interface ${table.mapperName} extends ${superMapperClass}<${entity}${poSuffix}> {

}
</#if>
