package com.liyuan.hong.showbooking;

import java.text.ParseException;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.liyuan.hong.showbooking.controller.AppAdminController;
import com.liyuan.hong.showbooking.controller.AppBuyerController;
import com.liyuan.hong.showbooking.controller.AppController;

@SpringBootApplication
public class ShowBookingApplication implements CommandLineRunner {

	private static final String BUYER = "buyer";
	private static final String ADMIN = "admin";
	private AppController appController;

	public static void main(String[] args) {
		SpringApplication.run(ShowBookingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		appController = new AppBuyerController();
		if (args != null && args.length <= 1) {
			displayWrongArgsErrorMsgAndExit();
		} else if (ADMIN.equalsIgnoreCase(args[0])) {
			System.out.println("You are logged in as an ADMIN");
			appController = new AppAdminController();
		} else if (BUYER.equalsIgnoreCase(args[0])) {
			System.out.println("You are logged in as a BUYER");
		} else {
			displayWrongArgsErrorMsgAndExit();
		}
		try (Scanner sc = new Scanner(System.in);) {
			while (true) {
				System.out.println("Please enter your command");
				String cmd = sc.nextLine().trim();
				if ("exit".equalsIgnoreCase(cmd) || "quit".equalsIgnoreCase(cmd)) {
					break;
				}
				try {
					String[] ins = parseCmd(cmd);
					appController.process(ins);
				} catch (Exception e) {
					displayWrongCmdErrorMsg();
				}
			}
		}
		System.out.print("Bye");
	}

	private void displayWrongArgsErrorMsgAndExit() {
		System.out.println(
				"Please run this app with exact one argument, either" + BUYER + " or " + ADMIN + " (case insensitive)");
		System.exit(0);
	}

	private String[] parseCmd(String cmd) throws Exception {
		String[] args = new String[4];
		String[] cmds = cmd.split(" ");
		if (cmds.length < 2) {
			throw new Exception("");
		}
		if ("setup".equalsIgnoreCase(cmds[0])) {
			args[0] = "setup";
			extractArgs(cmds, args, 3);
		} else if ("show".equalsIgnoreCase(cmds[0])) {
			args[0] = "show";
			extractArgs(cmds, args, 1);
		} else if ("remove".equalsIgnoreCase(cmds[0])) {
			args[0] = "remove";
			extractArgs(cmds, args, 2);
		} else if ("remove".equalsIgnoreCase(cmds[0])) {
			args[0] = "remove";
			extractArgs(cmds, args, 2);
		} else if ("remove".equalsIgnoreCase(cmds[0])) {
			args[0] = "remove";
			extractArgs(cmds, args, 1);
		} else if ("remove".equalsIgnoreCase(cmds[0])) {
			args[0] = "remove";
			extractArgs(cmds, args, 2);
		} else if ("remove".equalsIgnoreCase(cmds[0])) {
			args[0] = "remove";
			extractArgs(cmds, args, 3);
		} else {
			throw new Exception("");
		}
		return args;
	}

	private void extractArgs(String[] cmds, String[] args, int num) {
		switch (num) {
		case 3:
			args[3] = Integer.valueOf(cmds[3]).toString();
		case 2:
			args[2] = Integer.valueOf(cmds[2]).toString();
		case 1:
			args[1] = Integer.valueOf(cmds[1]).toString();
		}
	}

	private void displayWrongCmdErrorMsg() {
		// TODO Auto-generated method stub
		
	}

}
