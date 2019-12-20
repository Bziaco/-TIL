package test;

import test.RequestProcess.ProcessType.ApprType;

public class EnumSetExample {
	public static void main(String[] args) {
		
		ApprType enumApprType =  RequestProcess.ProcessType.find("1").process();
		RequestProcess.ReqeustType.find("3").reqProcess(enumApprType);
	}
}
