package com.example.demo.dto.member;

import com.example.demo.dto.common.enumeration.MemberStatusCd;
import lombok.Getter;
import org.apache.ibatis.type.Alias;

@Getter
@Alias("memberSelectResponse")
public class MemberSelectResponse {

	private String id;
	private String password;
	private MemberStatusCd memberStatusCd;

}

