package com.example.upgradedwolves.personality;

import java.util.stream.Stream;

import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.personality.expressions.Expressions;
import com.example.upgradedwolves.personality.expressions.PlayfulExpression;

public class PlayfulPersonality extends WolfPersonality {

    public PlayfulPersonality() {
        super(Behavior.Playful);        
    }

    @Override
    public int levelUpStatBonus(WolfStatsEnum stats) {    
        if(stats == WolfStatsEnum.Speed)
            return 2;
        return 0;
    }

    @Override
    public Stream<Class<? extends Expressions>> getExpressions() {        
        return Stream.of(PlayfulExpression.class);
    }

    @Override
    protected String getResourceName() {        
        return "playful";
    }
    
}
