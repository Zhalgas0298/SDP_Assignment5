import java.util.Comparator;
import java.util.List;

public class FastestRouteStrategy implements RouteStrategy {

    @Override
    public Route selectRoute(
            List<Route> routes,
            DeliveryRequest request
    ) {
        return RouteFilters.validRoutes(routes, request)
                .stream()
                .min(Comparator.comparingInt(
                        r -> r.estimatedMinutes()
                                + r.trafficLevel() * 5
                ))
                .orElseThrow();
    }

    @Override
    public String getStrategyName() {
        return "Fastest Route";
    }
}
