package com.xinkao.erp.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.xinkao.erp.common.mapper.BaseMapper;
import com.xinkao.erp.common.model.entity.DataEntity;
import com.xinkao.erp.common.service.BaseService;
import com.xinkao.erp.common.service.impl.BaseServiceImpl;

/**
 * <p>
 * 代码生成器（快速版本）
 * </p>
 */
public class FastCodeGenerator {

	// 基础信息配置
	// 数据库连接字符
	private static final String URL = "jdbc:mysql://139.9.129.215:3306/hz_asset?useUnicode=true&serverTimezone=UTC&useSSL=false&characterEncoding=utf8";
	// 数据库用户名
	private static final String USERNAME = "root";
	// 数据库密码
	private static final String PASSWORD = "b735c7b3d1f69a37";
	// 项目根路径
	private static final String projectRootPath = "E:\\test_project\\annotation";
	// 根包名称
	private static final String parentPackageName = "com.xinkao.erp";

	/**
	 * 执行此处
	 */
	public static void main(String[] args) {
		String author = "zzl";
		List<String> tableList = new ArrayList<String>();
		tableList.add("class_info");
		String busiModule = "classInfo";
		//tableList.add("course");
		//tableList.add("course_chapter");
		//tableList.add("course_resource");
		String removePrefix = "";
		completeGenerator(author,busiModule, tableList,removePrefix);
	}

	/**
	 * 【多模块使用】完整的实现方案
	 */
	protected static void completeGenerator(String author,String module, List<String> tableNameList,String removePrefix) {
		// 【1】六个文件的路径
		String entityPath = getEntityPath(module);
		String mapperPath = getMapperPath(module);
		String mapperXmlPath = getMapperXmlPath(module);
		String servicePath = getServicePath(module);
		String serviceImplPath = getServiceImplPath(module);
		String controllerPath = getControllerPath(module);
		// 【2】开始执行代码生成
		FastAutoGenerator.create(URL, USERNAME, PASSWORD)
				// 1. 全局配置
				.globalConfig(builder -> builder
						// 作者名称
						.author(author)
						// 开启覆盖已生成的文件。注释掉则关闭覆盖。请谨慎开启此选项！
						// .fileOverride()
						// 禁止打开输出目录。注释掉则生成完毕后，自动打开生成的文件目录。
						.disableOpenDir()
						// 开启swagger2。注释掉则默认关闭。
//						.enableSwagger()
						// 指定时间策略。
						.dateType(DateType.TIME_PACK)
						// 注释时间策略。
						.commentDate("yyyy-MM-dd HH:mm:ss"))
				// 2. 包配置
				.packageConfig(builder -> builder
						// 阶段1：各个文件的包名设置,用来拼接每个java文件的第一句：package com.XXX.XXX.XXX.xxx;
						// 父包名配置
						.parent(parentPackageName)
						// 输入模块名。
//						.moduleName(module)
						.entity(module+".entity").mapper(module+".mapper").service(module+".service").serviceImpl(module+".service.impl")
						.controller(module+".controller")
						.other("other")
						// 阶段2：所有文件的生成路径配置
						.pathInfo(new HashMap<OutputFile, String>() {
							{
								// 实体类的保存路径
								put(OutputFile.entity, entityPath);
								// mapper接口的保存路径
								put(OutputFile.mapper, mapperPath);
								// mapper.xml文件的保存路径
								put(OutputFile.xml, mapperXmlPath);
								// service层接口的保存路径
								put(OutputFile.service, servicePath);
								// service层接口实现类的保存路径
								put(OutputFile.serviceImpl, serviceImplPath);
								// 控制类的保存路径
								put(OutputFile.controller, controllerPath);
							}
						}))

				// 3. 策略配置
				.strategyConfig(builder -> builder.addInclude(tableNameList).addTablePrefix(removePrefix)
						// 阶段1：Entity实体类策略配置
						.entityBuilder()
						.superClass(DataEntity.class)
//						.superClass(BaseEntity.class)
						.disableSerialVersionUID().enableLombok()
						.enableTableFieldAnnotation()
//						.addSuperEntityColumns("id", "createTime", "createBy", "updateTime", "updateBy", "remark")
//						.addTableFills(new Column("create_time", FieldFill.INSERT))
//						.addTableFills(new Column("createBy", FieldFill.INSERT))
						// 会在实体类的该字段上追加注解[@TableField(value = "update_time", fill =
						// FieldFill.INSERT_UPDATE)]
//						.addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE))
//						.addTableFills(new Column("updateBy", FieldFill.INSERT_UPDATE))
						// 阶段2：Mapper策略配置
						.mapperBuilder()
						// 设置父类
						.superClass(BaseMapper.class)
						// 阶段3：Service策略配置
						.serviceBuilder()
						// 设置 service 接口父类
						.superServiceClass(BaseService.class)
						// 设置 service 实现类父类
						.superServiceImplClass(BaseServiceImpl.class)
						// 格式化 service 接口文件名称
						// 如果不设置，如表[sys_user]，默认的是[ISysUserService]。写成下面这种形式后，将变成[SysUserService]。
						.formatServiceFileName("%sService")
						// 格式化 service 实现类文件名称
						// 如果不设置，如表[sys_user]，默认的是[SysUserServiceImpl]。
						// .formatServiceImplFileName("%sServiceImpl")

						// 阶段4：Controller策略配置
						.controllerBuilder()
						// 设置父类。
						// 会集成此父类。
						// .superClass(BaseController.class)
						// 开启生成 @RestController 控制器
						// 会在控制类中加[@RestController]注解。
						.enableRestStyle()
						// 开启驼峰转连字符
						.enableHyphenStyle()
						// 最后：构建
						.build())

				// 模板引擎配置，默认 Velocity 可选模板引擎 Beetl 或 Freemarker
				// .templateEngine(new BeetlTemplateEngine())
				.templateEngine(new FreemarkerTemplateEngine())
				// 执行
				.execute();
	}

	/**
	 * 转换跟包的路径
	 * 
	 * @return
	 */
	private static String getParentPackagePath() {
		return StringUtils.replace(parentPackageName, ".", "/");
	}

	/**
	 * 获取实体类的路径
	 * 
	 * @return
	 */
	private static String getEntityPath(String module) {
		return projectRootPath + "/src/main/java/" + getParentPackagePath() +"/"+module+ "/entity";
	}

	/**
	 * 获取mapper的路径
	 * 
	 * @return
	 */
	private static String getMapperPath(String module) {
		return projectRootPath + "/src/main/java/" + getParentPackagePath()  +"/"+module+ "/mapper";
	}

	/**
	 * 获取mapper xml的路径
	 * 
	 * @return
	 */
	private static String getMapperXmlPath(String module) {
		return projectRootPath + "/src/main/resources/mapper/"+module;
	}

	/**
	 * 获取service的路径
	 * 
	 * @return
	 */
	private static String getServicePath(String module) {
		return projectRootPath + "/src/main/java/" + getParentPackagePath()  +"/"+module+ "/service";
	}

	/**
	 * 获取service impl的路径
	 * 
	 * @return
	 */
	private static String getServiceImplPath(String module) {
		return projectRootPath + "/src/main/java/" + getParentPackagePath() +"/"+ module +"/service/impl";
	}

//	/**
//	 * 获取controller的路径
//	 * 
//	 * @return
//	 */
	private static String getControllerPath(String module) {
		return projectRootPath + "/src/main/java/" + getParentPackagePath()  +"/"+module+"/controller";
	}

}
