package war3.pub.tokendemo.global;

/**
 * Created by turbo on 2017/2/20.
 */

public class TokenInvalideException extends RuntimeException {
    public TokenInvalideException() {
    }

    public TokenInvalideException(String message) {
        super(message);
    }
}
