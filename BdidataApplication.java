package com.bdi.sselab;
import com.bdi.sselab.dataLoad.DoLoad;
import com.bdi.sselab.repository.userDepart.RepositoriesInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Timer;

//关闭SpringBoot下DataSource的Jpa自动配置
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class
})
//显示启用TransactionManagement
@EnableTransactionManagement
@EnableSwagger2
public class BdidataApplication {

	@Autowired
	private RepositoriesInitializer repositoriesInitializer;

	@Bean
	public CommandLineRunner initialize(){
		return strings ->
				repositoriesInitializer.initialize();
	}

	public static void main(String[] args) {
		SpringApplication.run(BdidataApplication.class, args);
//		DoLoad doLoad =new DoLoad();
//		Timer timer = new Timer();
//        long delay = 0;
//        long intervalPeriod = 1 * 1000 ;
//        timer.scheduleAtFixedRate(doLoad.timerTask, delay, intervalPeriod);
		// pdf数据解析测试
		//TestPdf testPdf=new TestPdf("template/test3.pdf");
	}
}
