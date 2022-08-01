package ru.clevertec.ecl.interceptor.common;

/**
 * Common controller URL paths represented as strings.
 *
 * @author Olga Mailychko
 *
 */
public interface UrlPaths {
    //config
    String BASE_PACKAGES_TO_SCAN = "ru.clevertec.ecl";

    //common
    String PATH_CERTIFICATES = "/certificates";
    String PATH_ORDERS = "/orders";
    String PATH_TAGS = "/tags";
    String PATH_USERS = "/users";

    //actions
    String ACTION_FIND = "/find";
    String ACTION_FIND_ALL = "/findAll";
    String ACTION_BUY = "/buy";
    String ACTION_HEALTH_CHECK = "/status";
    String ACTION_SAVE = "/save";
    String ACTION_DELETE = "/delete";
    String ACTION_UPDATE = "/update";
    String ACTION_MOVE_SEQUENCE = "/sequence/next";
    String ACTION_SEQUENCE_CURRENT = "/sequence/current";
    String ACTION_SET_SEQUENCE = "/sequence/set";
}
