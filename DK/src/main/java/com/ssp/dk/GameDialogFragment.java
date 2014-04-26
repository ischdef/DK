/******************************************************************************
 * Copyright (c) 2014.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by Stefan Schulze on 2014/04/21.
 */
public class GameDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
    public interface GameDialogCallbacks {
        public void onGameDialogClick(int playerPosition, Game.eGameResult result, int score, boolean backButton);
    }

    // Instance of the interface to deliver action events
    GameDialogCallbacks mListener;

    // ShortCuts
    private Dialog mDialog;
    private View mDialogView;

    // Unique ID of selected Session
    long mSessionId;
    // Position of player in session
    int mPlayerPosition;
    // Values shown in Dialog window
    Game.eGameResult mPlayerGameResult;
    int mPlayerScore;


    public GameDialogFragment(long sessionId, int playerPosition, Game.eGameResult prevPlayerGameResult, int prevPlayerScore) {
        mSessionId = sessionId;
        mPlayerPosition = playerPosition;
        mPlayerGameResult = prevPlayerGameResult;
        mPlayerScore = prevPlayerScore;
    }

    private void recalculatePlayerScore() {
        NumberPicker numberPickerSign = (NumberPicker) mDialogView.findViewById(R.id.DialogAddGame_ScorePickerNegative);
        NumberPicker numberPicker1000 = (NumberPicker) mDialogView.findViewById(R.id.DialogAddGame_ScorePicker1000);
        NumberPicker numberPicker100  = (NumberPicker) mDialogView.findViewById(R.id.DialogAddGame_ScorePicker100);
        NumberPicker numberPicker10   = (NumberPicker) mDialogView.findViewById(R.id.DialogAddGame_ScorePicker10);
        NumberPicker numberPicker1    = (NumberPicker) mDialogView.findViewById(R.id.DialogAddGame_ScorePicker1);

        mPlayerScore = numberPicker1.getValue() +
                (numberPicker10.getValue() * 10) +
                (numberPicker100.getValue() * 100) +
                (numberPicker1000.getValue() * 1000);
        if (numberPickerSign.getValue() == 1) {
            mPlayerScore = mPlayerScore * (-1);
        }

        // Update score text view
        TextView scoreText = (TextView)mDialogView.findViewById(R.id.DialogAddGame_Score);
        scoreText.setText(getString(R.string.dialog_game_score) + " " + mPlayerScore);
    }

    private int getSingleDidgetForNumberPicker(int number, int exponent) {
        if (number < 0) {
            return ((-number) / exponent) % 10;
        } else {
            return (number / exponent) % 10;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the Listener so we can send events to the host
            mListener = (GameDialogCallbacks) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement PlayerAddDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get player parameter
        final Session session = SessionsList.getInstance().getSessionById(mSessionId);
        final long playerId = session.getSessionPlayerList().get(mPlayerPosition).getId();
        final Player player = PlayerList.getInstance().getPlayerById(playerId);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate custom dialog view
        // Pass null as the parent view because its going in the dialog layout
        mDialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_game, null);

        // Setup Numpickers
        NumberPicker numberPickerNegative = (NumberPicker) mDialogView.findViewById(R.id.DialogAddGame_ScorePickerNegative);
        numberPickerNegative.setMinValue(0);
        numberPickerNegative.setMaxValue(1);
        numberPickerNegative.setWrapSelectorWheel(false);
        numberPickerNegative.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int index) {
                if (index == 0) {
                    return "+";
                } else { // == 1
                    return "-";
                }
            }
        });
        numberPickerNegative.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                recalculatePlayerScore();
            }
        });
        if (mPlayerScore < 0) {
            numberPickerNegative.setValue(1);
        } else {
            numberPickerNegative.setValue(0);
        }

        NumberPicker numberPicker1000 = (NumberPicker) mDialogView.findViewById(R.id.DialogAddGame_ScorePicker1000);
        numberPicker1000.setMinValue(0);
        numberPicker1000.setMaxValue(9);
        numberPicker1000.setValue(getSingleDidgetForNumberPicker(mPlayerScore, 1000));
        numberPicker1000.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                recalculatePlayerScore();
            }
        });

        NumberPicker numberPicker100 = (NumberPicker) mDialogView.findViewById(R.id.DialogAddGame_ScorePicker100);
        numberPicker100.setMinValue(0);
        numberPicker100.setMaxValue(9);
        numberPicker100.setValue(getSingleDidgetForNumberPicker(mPlayerScore, 100));
        numberPicker100.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                recalculatePlayerScore();
            }
        });

        NumberPicker numberPicker10 = (NumberPicker) mDialogView.findViewById(R.id.DialogAddGame_ScorePicker10);
        numberPicker10.setMinValue(0);
        numberPicker10.setMaxValue(9);
        numberPicker10.setValue(getSingleDidgetForNumberPicker(mPlayerScore, 10));
        numberPicker10.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                recalculatePlayerScore();
            }
        });

        NumberPicker numberPicker1 = (NumberPicker) mDialogView.findViewById(R.id.DialogAddGame_ScorePicker1);
        numberPicker1.setMinValue(0);
        numberPicker1.setMaxValue(9);
        numberPicker1.setValue(getSingleDidgetForNumberPicker(mPlayerScore, 1));
        numberPicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                recalculatePlayerScore();
            }
        });

        // Set image and name from Player
        TextView playerNameText = (TextView)mDialogView.findViewById(R.id.DialogAddGame_Name);
        ImageView playerImageView = (ImageView)mDialogView.findViewById(R.id.DialogAddGame_Image);
        if (player.getImage() == null) {
            playerImageView.setImageDrawable(getResources().getDrawable(R.drawable.no_user_logo));
        } else {
            playerImageView.setImageDrawable(player.getImage());
        }
        playerNameText.setText(player.getName());

        // Set score view
        TextView scoreText = (TextView)mDialogView.findViewById(R.id.DialogAddGame_Score);
        scoreText.setText(getString(R.string.dialog_game_score) + " " + mPlayerScore);


        // Set Game Result radio button view
        RadioGroup buttonGroup = (RadioGroup)mDialogView.findViewById(R.id.DialogAddGame_Buttons);
        if (mPlayerGameResult == Game.eGameResult.LOST) {
            buttonGroup.check(R.id.DialogAddGame_ButtonLost);
        } else if (mPlayerGameResult == Game.eGameResult.WON) {
            buttonGroup.check(R.id.DialogAddGame_ButtonWon);
        } else { // Neutral
            buttonGroup.check(R.id.DialogAddGame_ButtonNeutral);
        }
        // store game result selection change
        buttonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.DialogAddGame_ButtonLost:
                        mPlayerGameResult = Game.eGameResult.LOST;
                        break;
                    case R.id.DialogAddGame_ButtonWon:
                        mPlayerGameResult = Game.eGameResult.WON;
                        break;
                    case R.id.DialogAddGame_ButtonNeutral:
                        mPlayerGameResult = Game.eGameResult.NEUTRAL;
                        break;
                    default:
                        // TODO error
                        break;
                }
            }
        });


        // Setup dialog specific parameters
        String dialogTitle = getString(R.string.dialog_game_title) + " (" + getString(R.string.dialog_game_player)
                + " " + (mPlayerPosition+1) + "/" + session.getNumberOfPlayers() + ")";
        String negButtonTitle = (mPlayerPosition == 0) ?
                getString(R.string.dialog_game_cancel) : getString(R.string.dialog_game_prev_player);
        String posButtonTitle = (mPlayerPosition + 1 == session.getNumberOfPlayers()) ?
                getString(R.string.dialog_game_ok) : getString(R.string.dialog_game_next_player);

        builder.setView(mDialogView)
                .setTitle(dialogTitle)
                .setPositiveButton(posButtonTitle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onGameDialogClick(mPlayerPosition, mPlayerGameResult, mPlayerScore, false);
                    }
                })
                .setNegativeButton(negButtonTitle, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onGameDialogClick(mPlayerPosition, mPlayerGameResult, mPlayerScore, true);
                    }
                });

        // Create dialog and save for later
        mDialog = builder.create();

        return mDialog;
    }
}
