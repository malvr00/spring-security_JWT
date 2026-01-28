package com.salt.hed_admin.common;

public class Const {

    public interface JWT {
        String BEARER = "Bearer ";

        String REFRESH_TOKEN = "refresh";
        String ACCESS_TOKEN = "access";

        String TOKEN_HEADER = "Authorization";
        String REFRESH_TOKEN_HEADER = "X-Refresh-Token";

        String CLAIM_ID = "id";
        String CLAIM_SID = "sid";
        String CLAIM_USER_ID = "userId";
        String CLAIM_USERNAME = "username";
        String CLAIM_ROLES = "roles";
        String CLAIM_TYPE = "type";

    }

    public interface ACTIVE {
        String ACTIVE_BASE = "spring.profiles.active";
        String ACTIVE_LIVE = "prod";
        String ACTIVE_DEV = "dev";
        String ACTIVE_LOCAL = "local";
    }

    public interface RESPONSE_STATUS {
        String SUCCESS = "success";
        String FAIL = "fail";
    }
}
