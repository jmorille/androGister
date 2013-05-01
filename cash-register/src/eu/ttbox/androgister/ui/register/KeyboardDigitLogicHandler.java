package eu.ttbox.androgister.ui.register;

public interface KeyboardDigitLogicHandler {

    void onDelete();

    void onClear();

    void onEnter();

    void onTextChanged();

    void insert(String text);

}
