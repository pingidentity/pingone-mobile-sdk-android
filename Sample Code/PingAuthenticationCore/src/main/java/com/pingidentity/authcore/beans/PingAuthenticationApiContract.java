package com.pingidentity.authcore.beans;
/*
 * See LICENSE.txt for the Ping Authentication licensing information.
 */
public class PingAuthenticationApiContract {

    public static final class PARAMS {
        public static final String SCOPE = "scope";
        public static final String RESPONSE_TYPE = "response_type";
        public static final String RESPONSE_MODE = "response_mode";
        public static final String CLIENT_ID = "client_id";
    }
    public static final String XSRF_HEADER = "X-XSRF-Header";
    public static final String CONTENT_TYPE_ACTION_HEADER = "Content-Type";
    public static final String COOKIE_HEADER = "Cookie";
    public static final String SET_COOKIE_HEADER = "Set-Cookie";

    /*
     * Beans contract
     */
    public static final class JSON {
        public static final String ID = "id";
        public static final String STATUS = "status";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String DEVICES = "devices";
        public static final String DEVICE_ID = "deviceRef";
        public static final String OTP = "otp";

        public static final String SERVER_PAYLOAD = "serverPayload";
        public static final String MOBILE_PAYLOAD = "mobilePayload";
        public static final String LINKS = "_links";

        public static final String AUTHORIZE_RESPONSE = "authorizeResponse";
        public static final String CODE = "code";
        public static final String GRANT_TYPE = "grant_type";
        public static final String CLIENT_ID = "client_id";
        public static final String CLIENT_SECRET = "client_secret";

        public static final String ACCESS_TOKEN = "access_token";
        public static final String ID_TOKEN = "id_token";
        public static final String TOKEN_TYPE = "token_type";
        public static final String EXPIRES_IN = "expires_in";

        public static final String ERROR = "error";
        public static final String ERROR_DESCRIPTION = "error_description";
        public static final String MESSAGE = "message";
        public static final String USER_MESSAGE = "userMessage";
        public static final String DETAILS = "details";
    }

    public static final class STATES {
        public static final String USERNAME_PASSWORD_REQUIRED = "USERNAME_PASSWORD_REQUIRED";
        public static final String AUTHENTICATION_REQUIRED = "AUTHENTICATION_REQUIRED";
        public static final String DEVICE_SELECTION_REQUIRED = "DEVICE_SELECTION_REQUIRED";
        public static final String OTP_REQUIRED = "OTP_REQUIRED";

        public static final String MOBILE_PAIRING_REQUIRED = "MOBILE_PAIRING_REQUIRED";
        public static final String PUSH_CONFIRMATION_WAITING = "PUSH_CONFIRMATION_WAITING";
        public static final String PUSH_CONFIRMATION_REJECTED = "PUSH_CONFIRMATION_REJECTED";
        public static final String PUSH_CONFIRMATION_TIMED_OUT = "PUSH_CONFIRMATION_TIMED_OUT";
        public static final String MFA_COMPLETED = "MFA_COMPLETED";
        /*
         * Indicates a dead end. The API client can proceed with the OIDC flow by calling
         * cancelAuthentication. The adapter will return FAILURE.
         */
        public static final String MFA_FAILED = "MFA_FAILED";
        public static final String COMPLETED = "COMPLETED";
        public static final String FAILED = "FAILED";

        public static final String MOBILE_PAIRING_STARTED = "MOBILE_PAIRING_STARTED";
        public static final String MOBILE_PAIRING_COMPLETED = "MOBILE_PAIRING_COMPLETED";
        public static final String TOKEN_EXCHANGE_COMPLETED = "TOKEN_EXCHANGE_COMPLETED";

        public static final String ERROR_RECEIVED = "ERROR_RECEIVED";
    }

    public static final class ACTIONS{

        public static final String SELF = "self";
        /*
         * Initializes the flow.
         */
        public static final String INITIALIZE = "authorization.oauth2";
        /*
         * Validates the provided OneTimePasscode
         */
        public static final String CHECK_OTP = "checkOtp";
        /*
         * Starts an authentication with the specific deviceId.
         * If there is an authentication in progress, this authentication will be canceled.
         * Only available when the user has at least one device
         * (This action can be used as “retry”)
         */
        public static final String SELECT_DEVICE = "selectDevice";
        /*
         * Starts an authentication
         */
        public static final String AUTHENTICATE = "authenticate";
        public static final String CANCEL_AUTHENTICATION = "cancelAuthentication";
        /*
         * Continue the current authentication flow.
         */
        public static final String CONTINUE_AUTHENTICATION = "continueAuthentication";
        public static final String CHECK_USERNAME_PASSWORD = "checkUsernamePassword";
        /*
         * Get status regarding the mobile push.
         */
        public static final String POLL = "poll";
        public static final String TOKEN_EXCHANGE = "application/x-www-form-urlencoded";
    }

    /*
     * Device Model Contract keys
     */
    public static final String DEVICE_ID = "id";
    public static final String DEVICE_TYPE = "type";
    public static final String DEVICE_TARGET = "target";
    public static final String DEVICE_NAME = "name";
    public static final String DEVICE_NICKNAME = "nickname";
    public static final String DEVICE_ROLE = "role";
    public static final String DEVICE_ENROLLMENT_TIME = "enrollmentTime";
    public static final String DEVICE_APP_ID = "applicationId";
    public static final String DEVICE_BYPASS_EXP = "bypassExpiration";
    public static final String DEVICE_IS_BYPASS = "bypassed";
    public static final String DEVICE_PUSH_ENABLED = "pushEnabled";
    public static final String DEVICE_OS_VERSION = "osVersion";
    public static final String DEVICE_APP_VERSION = "applicationVersion";
    public static final String DEVICE_IS_USABLE = "usable";

    /*
     * User Model Contract keys
     */
    public static final String USER = "user";
    public static final String USER_ID = "id";
    public static final String USER_FIRST_NAME = "firstName";
    public static final String USER_LAST_NAME = "lastName";
    public static final String USER_STATUS = "status";
    public static final String USER_LAST_LOGIN = "lastLogin";

    /*
     * Other contracts
     */
    public static final String STATE = "state";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
}
