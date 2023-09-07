package com.hmis.server.hmis.common.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DbInit implements CommandLineRunner {
	@Autowired
	private Runner runner;

	@Autowired
	private ModelSeeder runnerSeeder;

	@Override
	public void run(String... args) {
		this.runner.seedPermissions();
		this.runner.seedDefaultRole();
		this.runner.seedDefaultUser();
		this.runner.seedGlobalSettings();
		this.runner.seedDepartmentCategory();
		this.runner.seedRevenueDepartmentType();
		this.runner.seedPaymentMethod();
		this.runner.seedMobileMoneyLocation();
		this.runnerSeeder.seedAllBaseModel();
	}
}
