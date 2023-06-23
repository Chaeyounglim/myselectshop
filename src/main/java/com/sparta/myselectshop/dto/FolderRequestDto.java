package com.sparta.myselectshop.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class FolderRequestDto {
    List<String> folderNames;
    // 폴더 여러개 추가하기 위해 List 타입으로 요청 받음
}