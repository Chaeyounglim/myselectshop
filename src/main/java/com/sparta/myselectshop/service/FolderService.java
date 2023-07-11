package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.FolderResponseDto;
import com.sparta.myselectshop.entity.Folder;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    @Transactional
    public void addFolders(List<String> folderNames, User user) {
        // 사용자가 추가하고자 하는 폴더 이름 중에 이미 존재하는 폴더가 있는지 봐야 한다.
        // 폴더 테이블 내에 해당하는 유저의 폴더들 중에 추가하고자 요청한 폴더 이름과 같은 데이터가 있는지?

        // 해당 유저의 폴더 이름들 list 로 가져옴.
        List<Folder> existFolderList = folderRepository.findAllByUserAndNameIn(user,folderNames);

        // 중복되는지 확인
        List<Folder> folderList = new ArrayList<>();
        for (String folderName : folderNames) { // 추가 요청한 폴더 fot Each 문
            if(!isExistFolderName(folderName, existFolderList)) {
                // 중복 폴더가 없을 경우
                Folder folder = new Folder(folderName, user);
                folderList.add(folder);
            } else { // 중복 폴더가 있을 경우
                throw new IllegalArgumentException("중복된 폴더명을 제거해주세요! 폴더명: " + folderName);
            }
        }

        folderRepository.saveAll(folderList);
    }

    public List<FolderResponseDto> getFolders(User user) {
        List<Folder> folderList = folderRepository.findAllByUser(user);
        List<FolderResponseDto> responseDtoList = new ArrayList<>();


        for (Folder folder : folderList) {
            responseDtoList.add(new FolderResponseDto(folder));
        }
        return responseDtoList;
    }

    private boolean isExistFolderName(String folderName, List<Folder> existFolderList) {
        for (Folder existFolder : existFolderList) {
            if(folderName.equals(existFolder.getName())){
                return true;
            }
        }// end of for()
        return false;
    }

}
