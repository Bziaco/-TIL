package test;

import java.util.Map;

import test.RequestProcess.ProcessType.ApprType;
import util.EnumUtils;

public class RequestProcess {
	enum ReqeustType {
		WEBUSER("0"){
			@Override
			public void reqProcess(ApprType apprType) {
				
				if(apprType.equals(ApprType.RECOGNITION)) {
					System.out.println("(승인) 웹계정 신청입니다.");
				} else {
					System.out.println("(반려) 웹계정 신청입니다.");
				}
				
			}
		},
		ACCESSIP("1") {
			@Override
			public void reqProcess(ApprType apprType) {
				if(apprType.equals(ApprType.RECOGNITION)) {
					System.out.println("(승인) 접근주소 신청입니다.");
				} else {
					System.out.println("(반려) 접근주소 신청입니다.");
				}
			}
		},
		IDINVALIDDATE("2") {
			@Override
			public void reqProcess(ApprType apprType) {
				if(apprType.equals(ApprType.RECOGNITION)) {
					System.out.println("(승인) 계정유효기간 연장 신청입니다.");
				} else {
					System.out.println("(반려) 계정유효기간 연장 신청입니다.");
				}
			}
		},
		PASSWORDAUTH("3"){
			@Override
			public void reqProcess(ApprType apprType) {
				if(apprType.equals(ApprType.RECOGNITION)) {
					System.out.println("(승인) 패스워드 권한 승인 연장 신청입니다.");
				} else {
					System.out.println("(반려) 패스워드 권한 승인 연장 신청입니다.");
				}
			}
		},
		EMPTY("99"){
			@Override
			public void reqProcess(ApprType apprType) {
				System.out.println("유효하지 않은 신청입니다.");
			}
		};
		
		private final String type;
		
		ReqeustType(String type) {
			this.type = type;
		}
		public String getType() { return type; }
		
		public abstract void reqProcess(ApprType apprType);
		
		@Override
		public String toString() { return type; }
		
		public static ReqeustType find(String value) {
			return EnumUtils.findEnum(
					ReqeustType.class,
					type -> type.toString().equals(value),
					EMPTY
			);
		}
	}
	
	enum ProcessType {
		AND("1") {
			@Override
			ApprType process() {
				System.out.println("AND");
				
				int val = 1;
				
				if(val > 0) {
					return ApprType.RECOGNITION;
				} else {
					return ApprType.REJECT;
				}
			}
		},
		OR("2") {
			@Override
			ApprType process() {
				System.out.println("OR");
				
				int val = 0;
				
				if(val > 0) {
					return ApprType.RECOGNITION;
				} else {
					return ApprType.REJECT;
				}
			}
		};
		
		private final String type;
		ProcessType(String type) {
			this.type = type;
		}
		
		public String getType() { return type; }
		
		abstract ApprType process();
		@Override
		public String toString() { return type; }
		
		public static ProcessType find(String value) {
			return EnumUtils.findEnum(
					ProcessType.class,
					type -> type.toString().equals(value)
			);
		}
		
		enum ApprType {
			RECOGNITION, REJECT;
		}
	}
	
	
}
