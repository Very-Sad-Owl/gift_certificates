package ru.clevertec.ecl.util.localization;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.exception.InvalidArgException;
import ru.clevertec.ecl.exception.NoRequiredArgsException;
import ru.clevertec.ecl.exception.UndefinedException;
import ru.clevertec.ecl.exception.crud.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Component
public class MessageProvider {
    private final Map<String, String> messages = new HashMap<>();
    private static final String BUNDLE_BASE_TITLE = "messages";

    @PostConstruct
    public void init() {
        Locale locale = LocaleContextHolder.getLocale();
        ResourceBundle.clearCache();
        ResourceBundle rb = ResourceBundle.getBundle(BUNDLE_BASE_TITLE, locale);
        messages.put(NotFoundException.class.getSimpleName(), rb.getString(MessagesLocaleNames.NOT_FOUND_MSG));
        messages.put(SavingException.class.getSimpleName(), rb.getString(MessagesLocaleNames.SAVING_MSG));
        messages.put(UpdatingException.class.getSimpleName(), rb.getString(MessagesLocaleNames.UPDATING_MSG));
        messages.put(DeletionException.class.getSimpleName(), rb.getString(MessagesLocaleNames.DELETING_MSG));
        messages.put(InvalidArgException.class.getSimpleName(), rb.getString(MessagesLocaleNames.INVALID_ARG_MSG));
        messages.put(NoRequiredArgsException.class.getSimpleName(), rb.getString(MessagesLocaleNames.NO_ARGS_MSG));
        messages.put(UndefinedException.class.getSimpleName(), rb.getString(MessagesLocaleNames.UNDEFINED_EXCEPTION_MSG));
    }

    public String getMessage(String cause) {
        return messages.get(cause);
    }
}
