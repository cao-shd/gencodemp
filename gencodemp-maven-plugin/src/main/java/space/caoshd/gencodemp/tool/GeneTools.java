package space.caoshd.gencodemp.tool;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig.Builder;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.builder.Controller;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.builder.Mapper;
import com.baomidou.mybatisplus.generator.config.builder.Service;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import space.caoshd.common.po.BasePO;
import space.caoshd.common.service.IBaseService;
import space.caoshd.common.service.impl.BaseService;
import space.caoshd.gencodemp.util.PathUtils;
import space.caoshd.common.controller.BaseController;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class GeneTools {

    private final DataSource dataSource;
    private final String groupId;
    private final String artifactId;
    private final String moduleId;
    private final List<String> tableNames;
    private boolean supper;
    private boolean lombok;
    private boolean springdoc;
    private boolean swagger;
    private boolean override;

    public GeneTools(DataSource dataSource, String groupId, String artifactId, String moduleId, List<String> tableNames) {
        this.dataSource = dataSource;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.moduleId = moduleId;
        this.tableNames = tableNames;
    }

    public void generate() {

        // 加载配置文件
        String CONFIG_FILE_NAME = "generate.properties";
        Properties config = loadProperty(CONFIG_FILE_NAME);

        // 创建生成器对象 设置数据源
        FastAutoGenerator fastAutoGenerator = FastAutoGenerator.create(new Builder(dataSource));

        // 全局配置
        fastAutoGenerator.globalConfig(builder -> {
            String outputDir = javaDir();
            builder.outputDir(outputDir);
            builder.disableOpenDir();
            if (springdoc) {
                builder.enableSpringdoc();
            }
            if (swagger) {
                builder.enableSwagger();
            }
            builder.author(defaultIfBlank(config.getProperty("author"), "system"));
        });

        // 包配置
        fastAutoGenerator.packageConfig(builder -> {
            builder.parent(groupId + "." + artifactId);
            builder.moduleName(moduleId);
            builder.entity(defaultIfBlank(config.getProperty("pkg-po"), "po"));
            builder.entity(defaultIfBlank(config.getProperty("pkg-vo"), "vo"));
            builder.entity(defaultIfBlank(config.getProperty("pkg-bo"), "bo"));
            builder.controller(defaultIfBlank(config.getProperty("pkg-controller"), "controller"));
            builder.service(defaultIfBlank(config.getProperty("pkg-service"), "service"));
            builder.serviceImpl(defaultIfBlank(config.getProperty("pkg-service-impl"), "service.impl"));
            builder.mapper(defaultIfBlank(config.getProperty("pkg-mapper"), "mapper"));
            builder.pathInfo(createPathInfo(groupId, artifactId, moduleId));
        });

        // 模板配置
        fastAutoGenerator.templateConfig(builder -> builder.disable(TemplateType.ENTITY));
        // 模板注入配置
        fastAutoGenerator.injectionConfig(builder -> {
            builder.customMap(createCustomMap(groupId));
            builder.customFile(createCustomFile());
        });

        // mybatis mapper 生成策略配置
        fastAutoGenerator.strategyConfig(builder -> {
            Mapper.Builder mapperBuilder = builder.mapperBuilder();

            mapperBuilder.formatMapperFileName(defaultIfBlank(config.getProperty("format-mapper-file-name"), "I%sMapper"));
            mapperBuilder.enableFileOverride();
            mapperBuilder.enableBaseResultMap();
            mapperBuilder.enableBaseColumnList();
        });
        // mybatis entity 生成策略配置
        fastAutoGenerator.strategyConfig(builder -> {
            Entity.Builder entityBuilder = builder.entityBuilder();

            entityBuilder.formatFileName(defaultIfBlank(config.getProperty("format-file-name"), "%s"));
            String versionColumn = config.getProperty("column-name-version");
            entityBuilder.versionColumnName(defaultIfBlank(versionColumn, "version"));
            String logicDeleteColumn = defaultIfBlank(config.getProperty("column-name-logic-delete"), "is_delete");
            entityBuilder.logicDeleteColumnName(logicDeleteColumn);
            entityBuilder.enableRemoveIsPrefix();

            if (supper) {
                String superColumns = defaultIfBlank(config.getProperty("column-name-supper"),
                    "version,is_delete,create_time,update_time,update_by"
                );
                List<String> superEntityColumns = splitToList(superColumns);
                entityBuilder.addSuperEntityColumns(superEntityColumns);
                entityBuilder.superClass(BasePO.class);
            }

            entityBuilder.columnNaming(NamingStrategy.underline_to_camel);
            if (override) {
                entityBuilder.enableFileOverride();
            }

            if (lombok) {
                entityBuilder.enableLombok();
            }
        });

        // controller 生成策略配置
        fastAutoGenerator.strategyConfig(builder -> {
            Controller.Builder controllerBuilder = builder.controllerBuilder();
            if (supper) {
                controllerBuilder.superClass(BaseController.class);
            }
            controllerBuilder.enableRestStyle();
            controllerBuilder.enableHyphenStyle();
            controllerBuilder.enableFileOverride();
        });

        // service 生成策略配置
        fastAutoGenerator.strategyConfig(builder -> {
            Service.Builder serviceBuilder = builder.serviceBuilder();
            serviceBuilder.formatServiceImplFileName("%sService");
            if (supper) {
                serviceBuilder.superServiceClass(IBaseService.class);
                serviceBuilder.superServiceImplClass(BaseService.class);
            }
            serviceBuilder.enableFileOverride();
        });

        fastAutoGenerator.strategyConfig(builder -> {
            builder.addInclude(tableNames);
            String tablePrefix = config.getProperty("table-prefix");
            builder.addTablePrefix(splitToList(tablePrefix));
        });

        FreemarkerTemplateEngine freemarkerTemplateEngine = new FreemarkerTemplateEngine();
        fastAutoGenerator.templateEngine(freemarkerTemplateEngine);

        fastAutoGenerator.execute();
    }

    private List<String> splitToList(String property) {
        if (property != null && !property.trim().isEmpty()) {
            return Arrays.stream(property.split(",")).map(String::trim).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private Properties loadProperty(String propertyName) {
        try {
            ClassLoader classLoader = GeneTools.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(propertyName);
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<OutputFile, String> createPathInfo(String groupId, String artifactId, String moduleId) {
        Map<OutputFile, String> pathInfo = new HashMap<>();
        String mapperDir = mapperDir(groupId, artifactId, moduleId);
        pathInfo.put(OutputFile.xml, mapperDir);
        return pathInfo;
    }

    private List<CustomFile> createCustomFile() {
        return Arrays.asList(createPoFile(), createBoFile(), createVoFile());
    }

    private CustomFile createBoFile() {
        CustomFile.Builder builder = new CustomFile.Builder();
        builder.packageName("bo");
        builder.fileName("BO.java");
        builder.templatePath("/templates/bo.java.ftl");
        builder.enableFileOverride();
        return builder.build();
    }

    private CustomFile createPoFile() {
        CustomFile.Builder builder = new CustomFile.Builder();
        builder.packageName("po");
        builder.fileName("PO.java");
        builder.templatePath("/templates/po.java.ftl");
        builder.enableFileOverride();
        return builder.build();
    }

    private CustomFile createVoFile() {
        CustomFile.Builder builder = new CustomFile.Builder();
        builder.packageName("vo");
        builder.fileName("VO.java");
        builder.templatePath("/templates/vo.java.ftl");
        builder.enableFileOverride();
        return builder.build();
    }

    private Map<String, Object> createCustomMap(String groupId) {
        Map<String, Object> customMap = new HashMap<>();
        customMap.put("poPkg", "po");
        customMap.put("poSuffix", "PO");
        customMap.put("voPkg", "vo");
        customMap.put("voSuffix", "VO");
        customMap.put("boPkg", "bo");
        customMap.put("boSuffix", "BO");

        if (supper) {
            customMap.put("supperPo", "BasePO");
            customMap.put("supperPoPkg", superXoPkg(groupId, "po"));
            customMap.put("supperVo", "BaseVO");
            customMap.put("supperVoPkg", superXoPkg(groupId, "vo"));
            customMap.put("supperBo", "BaseBO");
            customMap.put("supperBoPkg", superXoPkg(groupId, "bo"));
        }
        return customMap;
    }

    private String userDir() {
        return System.getProperty("user.dir");
    }

    private String javaDir() {
        String userDir = userDir();
        return PathUtils.normalizePath(String.format("%s/src/main/java/", userDir));
    }

    private String mapperDir(String groupId, String artifactId, String moduleId) {
        String userDir = userDir();
        String groupPath = groupPath(groupId);
        String mapperDir = String.format("%s/src/main/java/%s/%s/%s/mapper/", userDir, groupPath, artifactId, moduleId);
        return PathUtils.normalizePath(mapperDir);
    }

    private String superXoPkg(String groupId, String pkg) {
        return String.format("%s.common.%s", groupId, pkg);
    }

    private String groupPath(String groupId) {
        return "/" + groupId.replaceAll("\\.", "/") + "/";
    }

    private String defaultIfBlank(String str, String defaultStr) {
        if (str == null || str.trim().isEmpty()) {
            return defaultStr;
        }
        return str;
    }

}
