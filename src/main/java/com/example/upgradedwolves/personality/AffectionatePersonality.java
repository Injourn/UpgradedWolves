package com.example.upgradedwolves.personality;

import java.util.stream.Stream;

import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.personality.expressions.AggressiveFollowExpression;
import com.example.upgradedwolves.personality.expressions.Expressions;

public class AffectionatePersonality extends WolfPersonality {

    public AffectionatePersonality() {
        super(Behavior.Affectionate);        
    }

    @Override
    public int levelUpStatBonus(WolfStatsEnum stats) {
        if(stats == WolfStatsEnum.Speed)
            return 2;
        return 0;
    }

    @Override
    public Stream<Class<? extends Expressions>> getExpressions() {        
        return Stream.of(AggressiveFollowExpression.class);
    }

    @Override
    protected String getResourceName() {
        return "affectionate";
    }
    
}
