package com.example.upgradedwolves.personality;

import java.util.stream.Stream;

import com.example.upgradedwolves.capabilities.WolfStatsEnum;
import com.example.upgradedwolves.personality.expressions.Expressions;
import com.example.upgradedwolves.personality.expressions.IntimidateExpression;

public class DominantPersonality extends WolfPersonality {

    public DominantPersonality() {
        super(Behavior.Dominant);        
    }

    @Override
    public int levelUpStatBonus(WolfStatsEnum stats) {
        if(stats == WolfStatsEnum.Intelligence)
            return 2;
        return 0;
    }

    @Override
    public Stream<Class<? extends Expressions>> getExpressions() {        
        return Stream.of(IntimidateExpression.class);
    }

    @Override
    protected String getResourceName() {        
        return "dominant";
    }
    
}
