package com.zearon.tvasistant;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;

/**
 * Created by zhiyuangong on 17/5/9.
 */
public class SoftKeyboard {
    static class KeyButtonInfo {
        public View button;
        public String keyText;

        public KeyButtonInfo(View button, String keyText) {
            this.button = button;
            this.keyText = keyText;
        }
    }

    private int SHIFT_STATUS = 0;
    private boolean SOFT_KEYBOARD_HIDDEN = false;


    private final ArrayList<Button> digitButtons = new ArrayList<>();
    private final ArrayList<Button> letterButtons = new ArrayList<>();
    private final ArrayList<KeyButtonInfo> otherButtons = new ArrayList<>();

    private ServerController controller = ServerController.getInstance();
    private View commandsPanel, keyboardABC, keyboardSymbols;
    private View shiftButton;

    public SoftKeyboard() {
    }

    public void initWithKeyButtons(Activity activity) {
        commandsPanel = activity.findViewById(R.id.commandsPanel);
        keyboardABC = activity.findViewById(R.id.softKeyBoard);
        keyboardSymbols = activity.findViewById(R.id.keyboardSymbolsPanel);

        // Add all digit key buttons to the digitButtons list
        digitButtons.add((Button) activity.findViewById(R.id.key_button_1));
        digitButtons.add((Button) activity.findViewById(R.id.key_button_2));
        digitButtons.add((Button) activity.findViewById(R.id.key_button_3));
        digitButtons.add((Button) activity.findViewById(R.id.key_button_4));
        digitButtons.add((Button) activity.findViewById(R.id.key_button_5));
        digitButtons.add((Button) activity.findViewById(R.id.key_button_6));
        digitButtons.add((Button) activity.findViewById(R.id.key_button_7));
        digitButtons.add((Button) activity.findViewById(R.id.key_button_8));
        digitButtons.add((Button) activity.findViewById(R.id.key_button_9));
        digitButtons.add((Button) activity.findViewById(R.id.key_button_0));

        // Add all letter key buttons to the letterButtons list
        letterButtons.add((Button) activity.findViewById(R.id.key_button_q));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_w));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_e));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_r));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_t));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_y));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_u));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_i));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_o));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_p));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_a));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_s));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_d));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_f));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_g));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_h));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_j));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_k));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_l));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_z));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_x));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_c));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_v));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_b));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_n));
        letterButtons.add((Button) activity.findViewById(R.id.key_button_m));


        // Other buttons:
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_escape), "Escape"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_return), "Return"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_backspace), "BackSpace"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.inputSwitchButton), "ctrl+space"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_space), "space"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_at), "at"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_underscore), "underscore"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_dash), "minus"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_dot), "period"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_comma), "comma"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_colon), "colon"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_slash), "slash"));
        // Symbol buttons:
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_asciitilde), "asciitilde"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_exclam), "exclam"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_at), "at"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_numbersign), "numbersign"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_dollar), "dollar"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_percent), "percent"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_asciicircum), "asciicircum"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_ampersand), "ampersand"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_parenleft), "parenleft"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_parenright), "parenright"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_asterisk), "asterisk"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_bar), "bar"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_grave), "grave"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_apostrophe), "apostrophe"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_quotedbl), "quotedbl"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_underscore), "underscore"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_slash), "slash"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_backslash), "backslash"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_bracketleft), "bracketleft"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_bracketright), "bracketright"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_braceleft), "braceleft"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_braceright), "braceright"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_colon), "colon"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_semicolon), "semicolon"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_comma), "comma"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_period), "period"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_less), "less"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_greater), "greater"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_equal), "equal"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_minus), "minus"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_plus), "plus"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_symbol_question), "question"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_Page_Up), "Page_Up"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_Page_Down), "Page_Down"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_arrow_Up), "Up"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_arrow_Left), "Left"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_arrow_Down), "Down"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_arrow_Right), "Right"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_Delete), "Delete"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_Escape), "Escape"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_Tab), "Tab"));
        otherButtons.add(new KeyButtonInfo(activity.findViewById(R.id.key_button_PrintScreen), "PrintScreen"));

        // Add event listeners for all digit key buttons
        for (Button digitButton : digitButtons) {
            digitButton.setOnClickListener(new View.OnClickListener() {
                String keyText = null;

                @Override
                public void onClick(View v) {
                    Button btn = (Button) v;
                    if (keyText == null) {
                        keyText = btn.getText().toString();
                    }
                    onDigitButtonClicked(btn, keyText);
                }
            });
        }

        // Add event listeners for all letter key buttons
        for (Button letterButton : letterButtons) {
            letterButton.setOnClickListener(new View.OnClickListener() {
                String keyText = null;

                @Override
                public void onClick(View v) {
                    Button btn = (Button) v;
                    if (keyText == null) {
                        keyText = btn.getText().toString().toLowerCase();
                    }
                    onLetterButtonClicked(btn, keyText);
                }
            });
        }

        // Add event listeners for all other key buttons
        for (KeyButtonInfo buttonInfo : otherButtons) {
            buttonInfo.button.setOnClickListener(v -> onOtherButtonClicked(buttonInfo) );
        }

        // Add event listener for shift key
        shiftButton = activity.findViewById(R.id.key_button_shift);
        shiftButton.setOnClickListener(v -> onShiftClicked(v));

        // Add event listener for command buttons
        activity.findViewById(R.id.volumeLowerButton).setOnClickListener(v -> onCommandLowerVolume());
        activity.findViewById(R.id.volumeRaiseButton).setOnClickListener(v -> onCommandRaiseVolume());

        // Add event listener for commands key
        activity.findViewById(R.id.switchToCommandsButton).setOnClickListener(v -> switchToCommands());
        activity.findViewById(R.id.switchToKeyboardButton).setOnClickListener(v -> switchToKeyboardABC());
        activity.findViewById(R.id.switchToSymbolsButton).setOnClickListener(v -> switchToKeyboardSymbols());
        activity.findViewById(R.id.switchToAlphaButton).setOnClickListener(v -> switchToKeyboardABC());

        // Soft keyboard toggle button
        if (SOFT_KEYBOARD_HIDDEN) {
            activity.findViewById(R.id.bottomPanel).setVisibility(View.GONE);
        }
        // Add event listener for toggle soft keyboard button
        activity.findViewById(R.id.toggleSoftKeyboardBtn).setOnClickListener(
                v -> onToggleSoftKeyboardBtnClicked(activity.findViewById(R.id.bottomPanel)));

        switchToCommands();
    }

    /**
     * Event Listener for digit key button press on soft keyboard.
     * @param btn Button clicked
     * @param keyText Key text for the button
     */
    private void onDigitButtonClicked(Button btn, String keyText) {
        Log.v("main", "Key Button Clicked: " + keyText);
        controller.sendKeyPress(keyText);
    }

    /**
     * Event Listener for letter key button press on soft keyboard.
     * @param btn Button clicked
     * @param keyText Key text for the button
     */
    private void onLetterButtonClicked(Button btn, String keyText) {
        if (SHIFT_STATUS == 1) {
            keyText = keyText.toUpperCase();

            // Change back to lower case
            SHIFT_STATUS = 0;
            changeKeyButtonTextCase(false);
        } else if (SHIFT_STATUS == 2) {
            keyText = keyText.toUpperCase();
        }

        Log.v("main", "Key Button Clicked: " + keyText);

        // Send key to server
        controller.sendKeyPress(keyText);
    }

    /**
     * Event Listener for other key button press on soft keyboard.
     * @param buttonInfo KeyButtonInfo
     */
    private void onOtherButtonClicked(KeyButtonInfo buttonInfo) {
        Log.v("main", "Key Button Clicked: " + buttonInfo.keyText);

        // Send key to server
        controller.sendKeyPress(buttonInfo.keyText);
    }

    /**
     * Event listener for shift key button press on soft keyboard.
     * @param btn Button clicked
     */
    private void onShiftClicked(View btn) {
        Log.v("main", "SHIFT Button Clicked");
        SHIFT_STATUS = (SHIFT_STATUS + 1) % 3;
        switch (SHIFT_STATUS) {
            case 0:
                changeKeyButtonTextCase(false);
                ((Button) shiftButton).setText("shift");
                break;
            case 1:
                changeKeyButtonTextCase(true);
                ((Button) shiftButton).setText("Shift");
                break;
            case 2:
                changeKeyButtonTextCase(true);
                ((Button) shiftButton).setText("SHIFT");
                break;
        }
    }

    /**
     * Change the text display for the soft keyboard.
     * @param uppercase If it is true, then display letters in upper case, otherwise, display
     *                  in lower case.
     */
    private void changeKeyButtonTextCase(boolean uppercase) {
        if (uppercase) {
            for (Button keyButton : letterButtons) {
                keyButton.setText(keyButton.getText().toString().toUpperCase());
            }
        } else {
            for (Button keyButton : letterButtons) {
                keyButton.setText(keyButton.getText().toString().toLowerCase());
            }
        }
    }

    private void onCommandLowerVolume() {
        controller.sendKeyPress("XF86AudioLowerVolume");
    }

    private void onCommandRaiseVolume() {
        controller.sendKeyPress("XF86AudioRaiseVolume");
    }

    public void onToggleSoftKeyboardBtnClicked(View bottomPanel) {
        SOFT_KEYBOARD_HIDDEN = ! SOFT_KEYBOARD_HIDDEN;
        if (SOFT_KEYBOARD_HIDDEN) {
            bottomPanel.setVisibility(View.GONE);
        } else {
            bottomPanel.setVisibility(View.VISIBLE);
        }
    }

    private void switchToCommands() {
        commandsPanel.setVisibility(View.VISIBLE);
        keyboardABC.setVisibility(View.GONE);
        keyboardSymbols.setVisibility(View.GONE);
    }

    private void switchToKeyboardABC() {
        commandsPanel.setVisibility(View.GONE);
        keyboardABC.setVisibility(View.VISIBLE);
        keyboardSymbols.setVisibility(View.GONE);
    }

    private void switchToKeyboardSymbols() {
        commandsPanel.setVisibility(View.GONE);
        keyboardABC.setVisibility(View.GONE);
        keyboardSymbols.setVisibility(View.VISIBLE);
    }
}
