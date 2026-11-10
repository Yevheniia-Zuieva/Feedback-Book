package sumdu.edu.ua.core.port;

public interface MailPort {
    void sendVerificationEmail(String email, String token);
}
