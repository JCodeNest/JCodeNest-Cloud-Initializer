package cn.jcodenest.framework.swagger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cn.jcodenest.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;

/**
 * Swagger 自动配置类, 基于 OpenAPI + Springdoc 实现
 *
 * @author JCodeNest
 * @version 1.0.0
 * @since 2025/7/28
 * <p>
 * Copyright (c) 2025 JCodeNest-Cloud-Initializer
 * All rights reserved.
 */
@AutoConfiguration
@ConditionalOnClass({OpenAPI.class})
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "springdoc.api-docs", name = "enabled", havingValue = "true", matchIfMissing = true)
public class JCodeSwaggerAutoConfiguration {

    // ========== 全局 OpenAPI 配置 ==========

    /**
     * 创建 OpenAPI 对象
     *
     * @param properties Swagger 属性配置类
     * @return OpenAPI 对象
     */
    @Bean
    public OpenAPI createApi(SwaggerProperties properties) {
        Map<String, SecurityScheme> securitySchemas = buildSecuritySchemes();

        OpenAPI openApi = new OpenAPI()
                .info(buildInfo(properties))
                .components(new Components().securitySchemes(securitySchemas))
                .addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION));

        securitySchemas.keySet().forEach(key -> openApi.addSecurityItem(new SecurityRequirement().addList(key)));
        return openApi;
    }

    /**
     * 创建 Info 对象
     *
     * @param properties Swagger 属性配置类
     * @return Info 对象
     */
    private Info buildInfo(SwaggerProperties properties) {
        return new Info()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .version(properties.getVersion())
                .contact(new Contact().name(properties.getAuthor()).url(properties.getUrl()).email(properties.getEmail()))
                .license(new License().name(properties.getLicense()).url(properties.getLicenseUrl()));
    }

    /**
     * 创建 SecurityScheme 对象
     *
     * @return SecurityScheme 对象
     */
    private Map<String, SecurityScheme> buildSecuritySchemes() {
        Map<String, SecurityScheme> securitySchemes = new HashMap<>();

        SecurityScheme securityScheme = new SecurityScheme()
                // 类型
                .type(SecurityScheme.Type.APIKEY)
                // 请求头的 name
                .name(HttpHeaders.AUTHORIZATION)
                // token 所在位置
                .in(SecurityScheme.In.HEADER);

        securitySchemes.put(HttpHeaders.AUTHORIZATION, securityScheme);
        return securitySchemes;
    }

    /**
     * 创建 OpenAPIService 对象
     *
     * @param openApi                   OpenAPI 对象
     * @param securityParser            SecurityService 对象
     * @param springDocConfigProperties SpringDocConfigProperties 对象
     * @param propertyResolverUtils     PropertyResolverUtils 对象
     * @param openApiBuilderCustomizers OpenApiBuilderCustomizer 对象
     * @param serverBaseUrlCustomizers  ServerBaseUrlCustomizer 对象
     * @param javadocProvider           JavadocProvider 对象
     * @return OpenAPIService 对象
     */
    @Bean
    @Primary // 以创建的 OpenAPIService Bean 为主, 避免一键改包后启动报错
    public OpenAPIService openApiBuilder(Optional<OpenAPI> openApi, SecurityService securityParser,
                                         SpringDocConfigProperties springDocConfigProperties,
                                         PropertyResolverUtils propertyResolverUtils,
                                         Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomizers,
                                         Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers,
                                         Optional<JavadocProvider> javadocProvider) {

        return new OpenAPIService(
                openApi,
                securityParser,
                springDocConfigProperties,
                propertyResolverUtils,
                openApiBuilderCustomizers,
                serverBaseUrlCustomizers,
                javadocProvider
        );
    }

    // ========== 分组 OpenAPI 配置 ==========

    /**
     * 创建 all 分组 OpenAPI 对象
     *
     * @return GroupedOpenApi 对象
     */
    @Bean
    public GroupedOpenApi allGroupedOpenApi() {
        return buildGroupedOpenApi("all", "");
    }

    /**
     * 创建 admin 分组 OpenAPI 对象
     *
     * @return GroupedOpenApi 对象
     */
    public static GroupedOpenApi buildGroupedOpenApi(String group) {
        return buildGroupedOpenApi(group, group);
    }

    /**
     * 创建 admin 分组 OpenAPI 对象
     *
     * @return GroupedOpenApi 对象
     */
    public static GroupedOpenApi buildGroupedOpenApi(String group, String path) {
        return GroupedOpenApi.builder()
                .group(group)
                .pathsToMatch("/admin-api/" + path + "/**", "/app-api/" + path + "/**")
                .addOperationCustomizer((operation, handlerMethod) -> operation
                        .addParametersItem(buildTenantHeaderParameter())
                        .addParametersItem(buildSecurityHeaderParameter()))
                .build();
    }

    /**
     * 创建租户编号 Header 参数
     *
     * @return Parameter 对象
     */
    private static Parameter buildTenantHeaderParameter() {
        return new Parameter()
                // header 名
                .name(HEADER_TENANT_ID)
                // 描述
                .description("租户编号")
                // 请求 header
                .in(String.valueOf(SecurityScheme.In.HEADER))
                // 默认：使用租户编号为 1
                .schema(new IntegerSchema()._default(1L).name(HEADER_TENANT_ID).description("租户编号"));
    }

    /**
     * 创建认证 Token Header 参数
     *
     * @return Parameter 对象
     */
    private static Parameter buildSecurityHeaderParameter() {
        return new Parameter()
                // header 名
                .name(HttpHeaders.AUTHORIZATION)
                // 描述
                .description("认证 Token")
                // 请求 header
                .in(String.valueOf(SecurityScheme.In.HEADER))
                // 默认：使用用户编号为 1
                .schema(new StringSchema()._default("Bearer test1").name(HEADER_TENANT_ID).description("认证 Token"));
    }
}
