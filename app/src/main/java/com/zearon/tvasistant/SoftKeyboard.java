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
    private int SHIFT_STATUS = 0;
    private final ArrayList<Button> keyButtons = new ArrayList<>();
    private final ArrayList<Button> digitButtons = new ArrayList<>();
    private final ArrayList<Button> letterButtons = new ArrayList<>();
    private final ArrayList<KeyButtonInfo> otherButtons = new ArrayList<>();

    public SoftKeyboard() {
    }

    public void initWithKeyButtons(Activity activity) {
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

        // Add all key buttons to the keyButtons list
        keyButtons.addAll(digitButtons);
        keyButtons.addAll(letterButtons);

        // Add event listeners for all key buttons
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

        // Add event listeners for all key buttons
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

        // Add event listener for shift key
        activity.findViewById(R.id.key_button_shift).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShiftClicked(v);
            }
        });
    }

    /**
     * Event Listener for key button press on soft keyboard.
     * @param btn Button clicked
     * @param keyText Key text for the button
     */
    private void onDigitButtonClicked(Button btn, String keyText) {
        Log.v("main", "Key Button Clicked: " + keyText);
    }

    /**
     * Event Listener for key button press on soft keyboard.
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
                break;
            case 1:
                changeKeyButtonTextCase(true);
                break;
            case 2:
                changeKeyButtonTextCase(true);
                break;
        }
    }

    /**
     * Event listener for backspace key button press on soft keyboard.
     * @param btnInfo KeyButtonInfo instance corresponding to the button clicked.
     */
    private void onOtherButtonClicked(KeyButtonInfo btnInfo) {
        //
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
}

class KeyButtonInfo {
    public Button button;
    public String keyText;

    public KeyButtonInfo(Button button, String keyText) {
        this.button = button;
        this.keyText = keyText;
    }
}
