/**
 * This file is part of Graylog Pipeline Processor.
 *
 * Graylog Pipeline Processor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog Pipeline Processor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog Pipeline Processor.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.plugins.pipelineprocessor.ast.expressions;

import com.google.common.base.Joiner;
import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.functions.Function;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;

public class FunctionExpression implements Expression {
    private final FunctionArgs args;
    private final Function<?> function;
    private final FunctionDescriptor descriptor;

    public FunctionExpression(FunctionArgs args) {
        this.args = args;
        this.function = args.getFunction();
        this.descriptor = this.function.descriptor();

        // precomputes all constant arguments to avoid dynamically recomputing trees on every invocation
        this.function.preprocessArgs(args);
    }

    public Function<?> getFunction() {
        return function;
    }

    public FunctionArgs getArgs() {
        return args;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public Object evaluate(EvaluationContext context) {
        return descriptor.returnType().cast(function.evaluate(args, context));
    }

    @Override
    public Class getType() {
        return descriptor.returnType();
    }

    @Override
    public String toString() {
        String argsString = "";
        if (args != null) {
            argsString = Joiner.on(", ")
                    .withKeyValueSeparator(": ")
                    .join(args.getArgs().entrySet().stream()
                                  .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                                  .iterator());
        }
        return descriptor.name() + "(" + argsString + ")";
    }
}