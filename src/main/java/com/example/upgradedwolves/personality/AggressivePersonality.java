package com.example.upgradedwolves.personality;

import java.util.stream.Stream;

import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.personality.expressions.Expressions;
import com.example.upgradedwolves.personality.expressions.IntimidateExpression;

public class AggressivePersonality extends WolfPersonality {

    public AggressivePersonality() {
        super(Behavior.Aggressive);
    }

    @Override
    public int levelUpStatBonus(WolfStatsEnum stats) {
        if(stats == WolfStatsEnum.Strength)
            return 2;
        return 0;
    }

    @Override
    public Stream<Class<? extends Expressions>> getExpressions() {        
        return Stream.of(IntimidateExpression.class);
    }

    @Override
    protected String getResourceName() {
        return "aggressive";
    }
    
}
