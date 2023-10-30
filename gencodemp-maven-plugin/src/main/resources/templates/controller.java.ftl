package ${package.Controller};

<#if !restControllerStyle>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>
import ${package.Parent}.${boPkg}.${entity}${boSuffix};
import ${package.Service}.I${entity}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
</#if>

import java.util.List;

/**
 * <p>
 * ${table.comment!}Controller
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("<#if package.ModuleName?? && package.ModuleName != "">/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>

    @Autowired
    private I${entity}Service<?, ${entity}BO> ${entity?uncap_first}Service;

    @GetMapping("/1")
    public ${entity}BO getById() {
        return ${entity?uncap_first}Service.findById(1L);
    }

    @GetMapping("/list")
    public List<${entity}BO> list() {
        return ${entity?uncap_first}Service.list();
    }



}

