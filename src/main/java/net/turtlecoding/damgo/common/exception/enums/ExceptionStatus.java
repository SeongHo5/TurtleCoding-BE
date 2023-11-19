package net.turtlecoding.damgo.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionStatus {

    // 400 - Bad Request : 잘못된 요청
    INVALID_INPUT_VALUE(400, "입력 값이 잘못되었습니다."),
    INVALID_TYPE_VALUE(400, "요청 타입이 잘못되었습니다."),
    INVALID_USER_ROLE(400, "존재하지 않는 권한입니다."),
    INVALID_USER_STATUS(400, "존재하지 않는 상태입니다."),

    // 401 - Unauthorized : 비인증(인증 수단이 없음)
    NO_AUTHORIZATION(401, "인증 정보가 없습니다."),
    INVALID_ID_IR_PW(401, "아이디 혹은 비밀번호가 틀렸습니다."),
    INVALID_AUTH_ERROR(401, "지원 되지 않거나 잘못된 인증 수단입니다."),
    INVALID_EMAIL_VERIFICATION_CODE(401, "이메일 인증 코드가 일치하지 않습니다."),

    // 403 - Forbidden : 권한 없음 (서버가 요청을 이해했지만 승인을 거부)
    NOT_COMMENT_OWNER(403, "해당 댓글의 소유자가 아닙니다."),
    NOT_POST_OWNER(403, "해당 게시글의 소유자가 아닙니다."),
    RESTRICTED_ACCOUNT(403, "이용 제한된 계정입니다."),
    RESTRICTED_USERNAME(403, "사용할 수 없는 이름입니다."),
    EMAIL_ALREADY_VERIFIED(403, "이미 인증된 이메일입니다."),
    EMAIL_ALREADY_SENT(403, "이미 이메일이 전송되었습니다. 3분 후에 다시 시도해주세요."),
    BLACKLISTED_TOKEN(403, "무효화된 토큰"),
    EXPIRED_TOKEN(403, "만료된 토큰"),
    EXPIRED_REFRESH_TOKEN(403, "리프레시 토큰 만료"),

    // 404 - Not Found : 잘못된 리소스 접근
    NOT_FOUND_ACCOUNT(404, "존재하지 않는 계정입니다."),
    NOT_FOUND_PRODUCT(404, "존재하지 않는 상품입니다."),
    NOT_FOUND_POST(404, "게시글이 존재하지 않습니다."),
    NOT_FOUND_COMMENT(404, "댓글이 존재하지 않습니다."),
    NOT_FOUND_CATEGORY(404, "카테고리가 존재하지 않습니다."),
    NOT_FOUND_TAG(404, "태그가 존재하지 않습니다."),
    NOT_FOUND_POST_TAG(404, "해당 게시글의 태그가 존재하지 않습니다."),

    // 405 - Method Not Allowed : 허용되지 않은 메소드
    METHOD_NOT_ALLOWED(405, "허용되지 않는 메소드입니다."),

    // 409 - Conflict : 중복 데이터
    CONFLICT_ACCOUNT(409, "이미 등록된 사용자 입니다."),
    DUPLICATED_TAG(409, "이미 등록된 태그 입니다."),
    DELETED_ACCOUNT(409, "이미 탈퇴한 계정입니다."),

    // 415 - Unsupported Media Type
    UNSUPPORTED_FILE_FORMAT(415, "지원하지 않는 파일 형식입니다."),

    // 500 - Internal Server Error : 서버 오류
    FAIL_TO_SEND_EMAIL(500, "이메일 전송 실패"),
    FAILED_TO_UPLOAD_FILE(500, "파일 업로드 실패");

    private final int statusCode;
    private final String message;
}
