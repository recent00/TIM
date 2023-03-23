package com.lys.generate;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
public class Application {

    public static void main(String[] args) {
        //我们需要构建一个代码生成器对象
        AutoGenerator mpg = new AutoGenerator();
        //怎么样去执行，配置策略
        //1、全局配置
        GlobalConfig gc = new GlobalConfig();
        //获取当前目录
        String projectPath = System.getProperty("user.dir");
        //输出目录
        gc.setOutputDir(projectPath+"generate/src/main/java");
        gc.setAuthor("lys");
        gc.setOpen(false);//创建完成后是否打开资源管理器
        gc.setFileOverride(false);//二次生成时，是否覆盖原文件
        gc.setIdType(IdType.ASSIGN_ID);//设置主键生成策略
        gc.setDateType(DateType.ONLY_DATE);
        gc.setSwagger2(true);
        mpg.setGlobalConfig(gc);
        //全局配置还支持通过set**Name来设置各**层**的名称，如果不做修改即为默认名称，与下面**包**的名称不同

        //2、设置数据源
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUsername("root");
        dsc.setPassword("123456");
        dsc.setUrl("jdbc:mysql://192.168.40.200:3306/IM?useSSL=false&serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");//驱动
        dsc.setDbType(DbType.MYSQL);//驱动类型
        mpg.setDataSource(dsc);
        //3、包的配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName("base"); //模块名称
        pc.setParent("com.lys.generate");//父级路径名称
        pc.setEntity("pojo");//实体类包名称
        pc.setMapper("mapper");//mapper包名称
        pc.setService("service");//service包名
        pc.setController("controller");//controller包名
        mpg.setPackageInfo(pc);
        //4、策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setInclude("im_msg_content","im_user","im_user_msg_box");//设置要映射的表名，二次映射是否覆盖可以通过全局策略中配置！
        strategy.setNaming(NamingStrategy.underline_to_camel);//数据库表明下划线转驼峰
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段转实体类名称方法，默认按照NamingStrategy转换
        strategy.setEntityLombokModel(false);//是否使用lombok开启注解
        strategy.setLogicDeleteFieldName("deleted");//逻辑删除字段
        //自动填充配置(自动填充也可以在数据库配置)
        TableFill createTime= new TableFill("create_time", FieldFill.INSERT);
        TableFill updateTime= new TableFill("update_time", FieldFill.INSERT_UPDATE);

        //乐观锁配置
        strategy.setVersionFieldName("version");//乐观锁字段
        strategy.setRestControllerStyle(true);//开启驼峰命名
        strategy.setControllerMappingHyphenStyle(true);
        mpg.setStrategy(strategy);
        mpg.execute();//执行
    }
}
