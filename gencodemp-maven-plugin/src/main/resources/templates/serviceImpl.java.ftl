package ${package.ServiceImpl};

import ${superServiceImplClassPackage};
import ${package.Parent}.${boPkg}.${entity}${boSuffix};
import ${package.Mapper}.${table.mapperName};
import ${package.Parent}.${poPkg}.${entity}${poSuffix};
import ${package.Service}.${table.serviceName};
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ${table.comment!} 服务实现类
 *
 * @author ${author}
 * @since ${date}
 */
@Service
<#if superServiceImplClass!="ServiceImpl">
public class ${entity}Service extends ${superServiceImplClass}<${entity}${poSuffix}, ${entity}${boSuffix}> implements I${entity}Service<${entity}${poSuffix}, ${entity}${boSuffix}> {
    public ${entity}Service(
        @Autowired ${table.mapperName} ${entity?uncap_first}Mapper
    ) {
        super.setBaseMapper(${entity?uncap_first}Mapper);
        super.setBoClass(${entity}BO.class);
        super.setPoClass(${entity}PO.class);
    }
<#else>
public class ${entity}Service extends ${superServiceImplClass}<${entity}${poSuffix}, ${entity}${boSuffix}> {
</#if>

}
