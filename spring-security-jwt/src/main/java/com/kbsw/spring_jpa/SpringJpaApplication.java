package com.kbsw.spring_jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = {"com.kbsw"})
@EntityScan(basePackages = {"com.kbsw.user.entity","com.kbsw.posts.entity", "com.kbsw.comment.entity", "com.kbsw.message.entity"}) //엔티티 패키지 기술
@EnableJpaRepositories(basePackages = {"com.kbsw.user.repository","com.kbsw.posts.repository", "com.kbsw.comment.repository", "com.kbsw.message.repository"})//리포지터리 패키지 기술
		//(exclude = DataSourceAutoConfiguration.class)
public class SpringJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringJpaApplication.class, args);
	}

}
