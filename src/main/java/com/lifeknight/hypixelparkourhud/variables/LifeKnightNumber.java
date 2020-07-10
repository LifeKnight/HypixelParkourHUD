package com.lifeknight.hypixelparkourhud.variables;

import static com.lifeknight.hypixelparkourhud.mod.Core.configuration;

public abstract class LifeKnightNumber extends LifeKnightVariable {
    protected final Number defaultValue;
    protected Number defaultMinimumValue;
    protected Number defaultMaximumValue;
    protected Number value;
    protected Number minimumValue;
    protected Number maximumValue;

    LifeKnightNumber(String name, String group, Number value) {
        super(name, group);
        this.defaultValue = value;
        this.value = value;
        defaultMinimumValue = Long.MIN_VALUE;
        defaultMaximumValue = Long.MAX_VALUE;
        minimumValue = Long.MIN_VALUE;
        maximumValue = Long.MAX_VALUE;
    }

    LifeKnightNumber(String name, String group, Number value, Number minimumValue, Number maximumValue) {
        super(name, group);
        this.defaultValue = value;
        this.value = value;
        defaultMinimumValue = minimumValue;
        defaultMaximumValue = maximumValue;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    @Override
    public Number getValue() {
        return value;
    };

    public double getAsDouble() {
        return value.doubleValue();
    }

    public void setValue(Number newValue) {
        value = newValue;
        if (configuration != null) {
            configuration.updateConfigurationFromVariables();
            onSetValue();
        }
    }

    public Number getMinimumValue() {
        return minimumValue;
    }

    public double getMinimumAsDouble() {
        return minimumValue.doubleValue();
    }

    public Number getMaximumValue() {
        return maximumValue;
    }

    public double getMaximumAsDouble() {
        return maximumValue.doubleValue();
    }

    public void setMinimumValue(Number minimumValue) {
        this.minimumValue = minimumValue;
        onSetMinimumValue();
    }

    public void setMaximumValue(Number maximumValue) {
        this.maximumValue = maximumValue;
        onSetMaximumValue();
    }

    public void onSetValue() {
    }

    public void onSetMinimumValue() {
    }

    public void onSetMaximumValue() {
    }

    @Override
    public void reset() {
        value = defaultValue;
        maximumValue = defaultMaximumValue;
        minimumValue = defaultMinimumValue;
    }

    @Override
    public String getCustomDisplayString() {
        if (iCustomDisplayString != null) {
            return iCustomDisplayString.customDisplayString();
        }
        return name + ": " + value;
    }

    public static class LifeKnightInteger extends LifeKnightNumber {
        public LifeKnightInteger(String name, String group, Integer value) {
            super(name, group, value);
        }

        public LifeKnightInteger(String name, String group, Integer value, Integer minimumValue, Integer maximumValue) {
            super(name, group, value);
            this.minimumValue = minimumValue;
            this.maximumValue = maximumValue;
        }
        

        @Override
        public Integer getValue() {
            return (Integer) super.getValue();
        }

        @Override
        public void setValue(Number newValue) {
            super.setValue(newValue.intValue());
        }
    }

    public static class LifeKnightDouble extends LifeKnightNumber {
        LifeKnightDouble(String name, String group, Double value) {
            super(name, group, value);
        }

        LifeKnightDouble(String name, String group, Double value, Double minimumValue, Double maximumValue) {
            super(name, group, value);
            this.minimumValue = minimumValue;
            this.maximumValue = maximumValue;
        }

        @Override
        public Double getValue() {
            return (Double) super.getValue();
        }

        @Override
        public void setValue(Number newValue) {
            super.setValue(newValue.doubleValue());
        }
    }

    public static class LifeKnightLong extends LifeKnightNumber {
        LifeKnightLong(String name, String group, Long value) {
            super(name, group, value);
        }

        LifeKnightLong(String name, String group, Long value, Long minimumValue, Long maximumValue) {
            super(name, group, value);
            this.minimumValue = minimumValue;
            this.maximumValue = maximumValue;
        }

        @Override
        public Long getValue() {
            return (Long) super.getValue();
        }

        @Override
        public void setValue(Number newValue) {
            super.setValue(newValue.longValue());
        }
    }
}
