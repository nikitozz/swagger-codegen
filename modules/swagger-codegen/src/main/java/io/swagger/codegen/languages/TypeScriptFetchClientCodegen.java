package io.swagger.codegen.languages;

import io.swagger.codegen.CliOption;
import io.swagger.codegen.CodegenConstants;
import io.swagger.codegen.CodegenModel;
import io.swagger.codegen.CodegenProperty;
import io.swagger.codegen.SupportingFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class TypeScriptFetchClientCodegen extends AbstractTypeScriptClientCodegen {

    public static final String NPM_NAME = "npmName";
    public static final String NPM_VERSION = "npmVersion";

    protected String npmName = null;
    protected String npmVersion = "1.0.0";
    protected Boolean supportsTS22 = false;

    public TypeScriptFetchClientCodegen() {
        super();

        // clear import mapping (from default generator) as TS does not use it
        // at the moment
        importMapping.clear();

        outputFolder = "generated-code/typescript-fetch";
        embeddedTemplateDir = templateDir = "TypeScript-Fetch";
        this.cliOptions.add(new CliOption(NPM_NAME, "The name under which you want to publish generated npm package"));
        this.cliOptions.add(new CliOption(NPM_VERSION, "The version of your npm package"));
        this.cliOptions.add(new CliOption(CodegenConstants.SUPPORTS_TS22, CodegenConstants.SUPPORTS_TS22_DESC).defaultValue("false"));
    }

    @Override
    public void processOpts() {
        super.processOpts();
        supportingFiles.add(new SupportingFile("api.mustache", "", "api.ts"));
        supportingFiles.add(new SupportingFile("git_push.sh.mustache", "", "git_push.sh"));
        supportingFiles.add(new SupportingFile("README.md", "", "README.md"));
        supportingFiles.add(new SupportingFile("package.json.mustache", "", "package.json"));
        supportingFiles.add(new SupportingFile("typings.json.mustache", "", "typings.json"));
        supportingFiles.add(new SupportingFile("tsconfig.json.mustache", "", "tsconfig.json"));
        supportingFiles.add(new SupportingFile("tslint.json.mustache", "", "tslint.json"));
        supportingFiles.add(new SupportingFile("gitignore", "", ".gitignore"));
        supportingFiles.add(new SupportingFile("configuration.mustache", "", "configuration.ts"));

        if(additionalProperties.containsKey(NPM_NAME)) {
            this.setNpmName(additionalProperties.get(NPM_NAME).toString());
        }

        if (additionalProperties.containsKey(NPM_VERSION)) {
            this.setNpmVersion(additionalProperties.get(NPM_VERSION).toString());
        }

        if (additionalProperties.containsKey(CodegenConstants.SUPPORTS_TS22)) {
            setSupportsTS22(Boolean.valueOf(additionalProperties.get(CodegenConstants.SUPPORTS_TS22).toString()));
            additionalProperties.put("supportsTS22", getSupportsTS22());
        }
    }

    @Override
    public String getName() {
        return "typescript-fetch";
    }

    @Override
    public String getHelp() {
        return "Generates a TypeScript client library using Fetch API (beta).";
    }

    public String getNpmName() {
        return npmName;
    }

    public void setNpmName(String npmName) {
        this.npmName = npmName;
    }

    public String getNpmVersion() {
        return npmVersion;
    }

    public void setNpmVersion(String npmVersion) {
        this.npmVersion = npmVersion;
    }

    public Boolean getSupportsTS22() {
        return supportsTS22;
    }

    public void setSupportsTS22(Boolean value) {
        this.supportsTS22 = value;
    }

    @Override
    public Map<String, Object> postProcessModels(Map<String, Object> objs) {
        // process enum in models
        List<Object> models = (List<Object>) postProcessModelsEnum(objs).get("models");
        for (Object _mo : models) {
            Map<String, Object> mo = (Map<String, Object>) _mo;
            CodegenModel cm = (CodegenModel) mo.get("model");
            cm.imports = new TreeSet(cm.imports);
            for (CodegenProperty var : cm.vars) {
                // name enum with model name, e.g. StatuEnum => PetStatusEnum
                if (Boolean.TRUE.equals(var.isEnum)) {
                    var.datatypeWithEnum = var.datatypeWithEnum.replace(var.enumName, cm.classname + var.enumName);
                    var.enumName = cm.classname + var.enumName;
                }
            }
        }

        return objs;
    }

}
