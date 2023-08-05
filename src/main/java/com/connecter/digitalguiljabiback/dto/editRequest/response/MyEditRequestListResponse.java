package com.connecter.digitalguiljabiback.dto.editRequest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class MyEditRequestListResponse {
    private int cnt;
    private List<MyEditRequestResponse> list;
}
