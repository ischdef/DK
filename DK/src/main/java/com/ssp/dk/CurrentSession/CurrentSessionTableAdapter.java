/******************************************************************************
 * Copyright (c) 2015.                                                        *
 * Stefan Schulze Programs.                                                   *
 ******************************************************************************/

package com.ssp.dk.CurrentSession;

/**
 * Created by Stefan on 30.05.2015.
 */

import com.ssp.dk.R;
import com.ssp.dk.Table.BaseTableAdapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class CurrentSessionTableAdapter extends BaseTableAdapter {
    private final Context mContext;
    private final LayoutInflater mInflater;

    private final int mWidthName;
    private final int mWidthScore;
    private final int mHeight;

    /**
     * Constructor
     *
     * @param context The current context.
     */
    public CurrentSessionTableAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);

        Resources resources = context.getResources();
        mWidthName = resources.getDimensionPixelSize(R.dimen.current_session_table_cell_width_name);
        mWidthScore = resources.getDimensionPixelSize(R.dimen.current_session_table_cell_width_score);
        mHeight = resources.getDimensionPixelSize(R.dimen.current_session_table_cell_height);
    }

    /**
     * Returns the context associated with this array adapter. The context is
     * used to create views from the resource passed to the constructor.
     *
     * @return The Context associated with this adapter.
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Quick access to the LayoutInflater instance that this Adapter retreived
     * from its Context.
     *
     * @return The shared LayoutInflater.
     */
    public LayoutInflater getInflater() {
        return mInflater;
    }

    @Override
    public View getView(int row, int column, View converView, ViewGroup parent) {
        if (converView == null) {
            converView = mInflater.inflate(getLayoutResource(row, column), parent, false);
        }
        setText(converView, getCellString(row, column));
        return converView;
    }

    /**
     * Sets the text to the view.
     *
     * @param view
     * @param text
     */
    private void setText(View view, String text) {
        ((TextView) view.findViewById(android.R.id.text1)).setText(text);
    }

    @Override
    public int getRowCount() {
        // TODO return num of games
        return 5;
    }

    @Override
    public int getColumnCount() {
        // TODO return num of players
        return 5;
    }

    @Override
    public int getWidth(int column) {
        // TODO return max. width of given column (longest playername or longest score) in pixels
        /* Example:
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(text,0,text.length(),bounds);
        int width = bounds.width();  */

        if (column < 0) {
            return mWidthName;
        } else {
            return mWidthScore;
        }
    }

    @Override
    public int getHeight(int row) {
        return mHeight;
    }

    /**
     * @param row    the title of the row of this header. If the column is -1
     *               returns the title of the row header.
     * @param column the title of the column of this header. If the column is -1
     *               returns the title of the column header.
     * @return the string for the cell [row, column]
     */
    public String getCellString(int row, int column) {
        String cellText;
        if (row < 0) {
            // Table head
            if (column < 0) {
                // Upper-left corner
                cellText = mContext.getString(R.string.current_session_table_game_number);
            } else {
                cellText = "#" + (column + 1);
            }
        }
        else {
            if (column < 0) {
                // TODO header string
                cellText = "Player name" + row;
            } else {
                // TODO add score or header string
                return "Score: " + row + "/" + column;
            }
        }

        return cellText;
    }

    public int getLayoutResource(int row, int column) {
        final int layoutResource;
        switch (getItemViewType(row, column)) {
            case 0:
                layoutResource = R.layout.current_session_table_header;
                break;
            case 1:
                layoutResource = R.layout.current_session_table_cell;
                break;
            default:
                throw new RuntimeException("CurrentSessionTableAdapter: Invalid getItemViewType() result");
        }
        return layoutResource;
    }

    @Override
    public int getItemViewType(int row, int column) {
        if (row < 0) {
            // table header
            return 0;
        } else {
            // normal table cell
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}

