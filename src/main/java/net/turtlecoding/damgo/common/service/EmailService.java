package net.turtlecoding.damgo.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.turtlecoding.damgo.common.exception.AuthException;
import net.turtlecoding.damgo.common.exception.ServiceFailedException;
import net.turtlecoding.damgo.common.exception.enums.ExceptionStatus;
import net.turtlecoding.damgo.common.service.template.EmailTemplate;
import net.turtlecoding.damgo.common.utils.RedisUtil;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static net.turtlecoding.damgo.common.constant.EmailConstant.*;
import static net.turtlecoding.damgo.common.exception.enums.ExceptionStatus.INVALID_EMAIL_VERIFICATION_CODE;
import static net.turtlecoding.damgo.common.utils.CodeGenerator.generateRandomCode;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final EmailTemplate emailTemplate;
    private final RedisUtil redisUtil;

    private void sendMail(
            final String recipient,
            final String subject,
            final String content
    ) {
        MimeMessage message = emailSender.createMimeMessage();

        try {
            message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject, ENCODING_CHARSET);
            message.setText(content, ENCODING_CHARSET, MAILER_SUBTYPE);
        } catch (MessagingException e) {
            throw new ServiceFailedException(ExceptionStatus.FAIL_TO_SEND_EMAIL);
        }
        emailSender.send(message);
    }

    /**
     * 이미 발송된 코드가 있는지 확인하고, 없으면 인증 코드 생성 후 이메일 전송
     * <p>
     * <em>사용시 주의 사항</em>
     * <p>
     * ※ 이메일 발송에 약 7초 정도 소요됩니다.
     *
     * @param email 인증 코드를 전송할 이메일
     */
    public void sendVerificationMail(final String email) {
        checkIfEmailIsWhiteListed(email);
        checkIfCodeAlreadySent(email, PREFIX_VERIFY);

        String verificationCode = generateRandomCode();
        redisUtil.setDataExpire(PREFIX_VERIFY + email, verificationCode, VERIFICATION_CODE_EXPIRE_TIME);
        sendMail(email, emailTemplate.VERIFICATION_TITTLE, emailTemplate.createVerificationMessage(verificationCode));
    }

    public void sendPasswordResetMail(final String email) {
        checkIfCodeAlreadySent(email, PREFIX_PW_RESET);
        String verificationCode = generateRandomCode();
        redisUtil.setDataExpire(PREFIX_PW_RESET + email, verificationCode, VERIFICATION_CODE_EXPIRE_TIME);
        sendMail(email, emailTemplate.PW_RESET_TITTLE, emailTemplate.createPasswordResetMessage(verificationCode));
    }

    /**
     * 본인 인증 코드 검증
     * <p>
     * 인증에 성공하면 1일간 인증된 이메일로 등록
     * @param email 인증 코드를 보냈던 이메일
     * @param code  검증할(사용자가 입력한) 인증 코드
     */
    public void verifyEmail(final String email, final String code) {
        String validCode = redisUtil.getData(PREFIX_VERIFY + email);
        if (!code.equals(validCode)) {
            throw new AuthException(INVALID_EMAIL_VERIFICATION_CODE);
        }
        redisUtil.setDataExpire(PREFIX_VERIFIED + email, VALUE_TRUE, WHITE_LIST_VERIFIED_TIME);
        redisUtil.deleteData(PREFIX_VERIFY + email);
    }

    /**
     * 비밀번호 재설정 코드 검증
     * @return 검증 성공 여부
     */
    public boolean verifyPasswordResetEmail(final String email, final String code) {
        String validCode = redisUtil.getData(PREFIX_PW_RESET + email);
        if (!code.equals(validCode)) {
            throw new AuthException(INVALID_EMAIL_VERIFICATION_CODE);
        }
        redisUtil.deleteData(PREFIX_PW_RESET + email);
        return true;
    }

    // ============ PRIVATE METHODS ============

    private void checkIfCodeAlreadySent(final String email, final String prefix) {
        if (redisUtil.hasKey(prefix + email)) {
            throw new AuthException(ExceptionStatus.EMAIL_ALREADY_SENT);
        }
    }

    private void checkIfEmailIsWhiteListed(final String email) {
        if (redisUtil.hasKey(PREFIX_VERIFIED + email)) {
            throw new AuthException(ExceptionStatus.EMAIL_ALREADY_VERIFIED);
        }
    }
}
