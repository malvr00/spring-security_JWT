package com.salt.hed_admin.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PagingInfoVO implements Serializable {

    private int totalPageCount;
    private long totalItemCount;
    private int page;
    private int size;

}
