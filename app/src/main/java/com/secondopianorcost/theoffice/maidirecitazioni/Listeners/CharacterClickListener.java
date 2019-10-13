package com.secondopianorcost.theoffice.maidirecitazioni.Listeners;

import android.widget.ImageView;
import com.secondopianorcost.theoffice.maidirecitazioni.Entities.Character;

/**
 * Created by giuse on 23/12/2017.
 */

public interface CharacterClickListener {
    void onCharacterItemClick(int pos, Character character, ImageView shareImageView);
}
