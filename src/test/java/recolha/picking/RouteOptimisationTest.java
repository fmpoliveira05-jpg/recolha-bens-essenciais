package recolha.picking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.estg.core.AidBox;
import org.junit.jupiter.api.Test;
import recolha.core.AidBoxImp;
import recolha.core.Distance;
import recolha.core.InstitutionImp;

/**
 * Otimização da ordem das paragens (vizinho mais próximo + 2-opt).
 */
class RouteOptimisationTest {

    /**
     * Quatro caixas nos cantos de um quadrado de 1000 m, com a base no meio de um dos lados.
     * O vizinho mais próximo cruza o quadrado; o 2-opt tem de chegar ao perímetro.
     */
    private static InstitutionImp square(AidBoxImp[] boxes) throws Exception {
        InstitutionImp inst = new InstitutionImp("Teste");
        double[][] xy = {{0, 0}, {1000, 0}, {1000, 1000}, {0, 1000}};
        String[] codes = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            boxes[i] = new AidBoxImp(codes[i], null);
            inst.addAidBox(boxes[i]);
            inst.addBaseDistance(new Distance(codes[i], Math.hypot(xy[i][0] - 480, xy[i][1]), 1));
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i != j) {
                    boxes[i].addDistance(new Distance(codes[j], Math.hypot(xy[i][0] - xy[j][0], xy[i][1] - xy[j][1]), 1));
                }
            }
        }
        return inst;
    }

    @Test
    void twoOptNeverMakesTheRouteLonger() throws Exception {
        AidBoxImp[] boxes = new AidBoxImp[4];
        InstitutionImp inst = square(boxes);
        AidBox[] crossed = {boxes[0], boxes[2], boxes[1], boxes[3]};

        AidBox[] improved = RouteGeneratorImp.twoOpt(crossed, inst);

        assertTrue(RouteGeneratorImp.routeLength(improved, inst) <= RouteGeneratorImp.routeLength(crossed, inst));
    }

    @Test
    void twoOptFindsThePerimeterOfTheSquare() throws Exception {
        AidBoxImp[] boxes = new AidBoxImp[4];
        InstitutionImp inst = square(boxes);
        AidBox[] crossed = {boxes[0], boxes[2], boxes[1], boxes[3]};

        double optimum = 480 + 1000 + 1000 + 1000 + Math.hypot(1000 - 480, 0);
        double length = RouteGeneratorImp.routeLength(RouteGeneratorImp.twoOpt(crossed, inst), inst);

        assertEquals(optimum, length, 1e-6);
    }

    @Test
    void emptyAndSingleStopRoutesAreLeftAlone() throws Exception {
        AidBoxImp[] boxes = new AidBoxImp[4];
        InstitutionImp inst = square(boxes);

        assertEquals(0, RouteGeneratorImp.twoOpt(new AidBox[0], inst).length);
        assertEquals(boxes[1], RouteGeneratorImp.twoOpt(new AidBox[] {boxes[1]}, inst)[0]);
    }
}
