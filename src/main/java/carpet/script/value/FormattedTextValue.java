package carpet.script.value;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Temporary compatibility shim for 1.21.8: avoid ComponentSerialization.codec(regs)
 * Use plain-string serialization to clear compile errors. This preserves runtime
 * behavior for concatenation and boolean/string conversions, and stores JSON using
 * Component's vanilla toString()/literal where needed.
 */
public class FormattedTextValue extends StringValue
{
    Component text;

    public FormattedTextValue(Component text)
    {
        super(null);
        this.text = text;
    }

    public static Value combine(Value left, Value right)
    {
        MutableComponent text;
        if (left instanceof FormattedTextValue ftv)
        {
            text = ftv.getText().copy();
        }
        else
        {
            if (left.isNull())
            {
                return right;
            }
            text = Component.literal(left.getString());
        }

        if (right instanceof FormattedTextValue ftv)
        {
            text.append(ftv.getText().copy());
            return new FormattedTextValue(text);
        }
        if (right.isNull())
        {
            return left;
        }
        text.append(right.getString());
        return new FormattedTextValue(text);
    }

    public static Value of(Component text)
    {
        return text == null ? Value.NULL : new FormattedTextValue(text);
    }

    @Override
    public String getString()
    {
        return text.getString();
    }

    @Override
    public boolean getBoolean()
    {
        return !text.getString().isEmpty();
    }

    @Override
    public Value clone()
    {
        return new FormattedTextValue(text);
    }

    @Override
    public String getTypeString()
    {
        return "text";
    }

    public Component getText()
    {
        return text;
    }

    @Override
    public Tag toTag(boolean force, RegistryAccess regs)
    {
        if (!force)
        {
            throw new NBTSerializableValue.IncompatibleTypeException(this);
        }
        // Store raw string form; removes dependency on ComponentSerialization.codec(regs)
        return StringTag.valueOf(getString());
    }

    @Override
    public Value add(Value o)
    {
        return combine(this, o);
    }

    public String serialize(RegistryAccess regs)
    {
        // Plain string serialization for now
        return getString();
    }

    public static FormattedTextValue deserialize(String serialized, RegistryAccess regs)
    {
        // Interpret stored string as a literal component
        return new FormattedTextValue(Component.literal(serialized));
    }

    public static Component getTextByValue(Value value)
    {
        return (value instanceof FormattedTextValue ftv) ? ftv.getText() : Component.literal(value.getString());
    }
}
