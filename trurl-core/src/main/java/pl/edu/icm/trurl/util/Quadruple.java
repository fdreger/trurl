/*
 * Copyright (c) 2022 ICM Epidemiological Model Team at Interdisciplinary Centre for Mathematical and Computational Modelling, University of Warsaw.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package pl.edu.icm.trurl.util;


import java.util.Objects;

public class Quadruple<FIRST, SECOND, THIRD, FOURTH> {
    public final FIRST first;
    public final SECOND second;
    public final THIRD third;

    public final FOURTH fourth;
    private Quadruple(FIRST first, SECOND second, THIRD third, FOURTH fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public static<FIRST, SECOND, THIRD, FOURTH> Quadruple<FIRST, SECOND, THIRD, FOURTH> of(FIRST first, SECOND second, THIRD third, FOURTH fourth) {
        return new Quadruple<>(first, second, third, fourth);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quadruple<?, ?, ?, ?> quadruple = (Quadruple<?, ?, ?, ?>) o;
        return first.equals(quadruple.first) && second.equals(quadruple.second) && third.equals(quadruple.third) && fourth.equals(quadruple.fourth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third, fourth);
    }
}
