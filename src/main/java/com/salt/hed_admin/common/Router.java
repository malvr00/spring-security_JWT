package com.salt.hed_admin.common;

public class Router {

    public interface API_VERSION {
        String V1 = "/v1";
        String API = "/api";

        String V1_BASE_PATH = V1 + API;
    }

    public interface BASE_URL {
        String BASE = "/hed";

        String ADMIN = "/admin";
    }

    public interface ACTUATOR {
        String HEALTH_CHECK = "/actuator/health";
    }

    public interface ERROR_URI {
        String ERROR = "/error/**";
    }

    public interface SWAGGER_URI {
        String AUTH_1 = "/swagger-ui/**";
        String AUTH_2 = "/v3/api-docs/swagger-config";
        String AUTH_3 = "/v3/api-docs";
    }

    public interface CERTIFIED_URI {
        String SIGN = "/signup";
        String LOGIN = "/login";
        String LOGOUT = "/logout";
    }

    public interface USER_URI {
        String BASE = "/users";

        String REFRESH = "/refresh";
    }

}
