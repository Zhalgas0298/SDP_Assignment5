import java.util.Comparator;
import java.util.List;

public class BalancedRouteStrategy implements RouteStrategy {

    private final double timeWeight;
    private final double costWeight;
    private final double distanceWeight;
    private final double emissionWeight;

    public BalancedRouteStrategy(
            double timeWeight,
            double costWeight,
            double distanceWeight,
            double emissionWeight
    ) {
        double sum = timeWeight + costWeight
                + distanceWeight + emissionWeight;

        if (!Double.isFinite(sum)
                || Math.abs(sum - 1.0) > 0.000001
                || !Double.isFinite(timeWeight)
                || !Double.isFinite(costWeight)
                || !Double.isFinite(distanceWeight)
                || !Double.isFinite(emissionWeight)
                || timeWeight < 0 || costWeight < 0
                || distanceWeight < 0 || emissionWeight < 0) {
            throw new IllegalArgumentException(
                    "Weights must be non-negative and sum to 1.0"
            );
        }

        this.timeWeight = timeWeight;
        this.costWeight = costWeight;
        this.distanceWeight = distanceWeight;
        this.emissionWeight = emissionWeight;
    }

    @Override
    public Route selectRoute(
            List<Route> routes,
            DeliveryRequest request
    ) {
        List<Route> valid = RouteFilters.validRoutes(routes, request);

        double maxTime = valid.stream()
                .mapToDouble(Route::estimatedMinutes)
                .max().orElse(1);

        double maxCost = valid.stream()
                .mapToDouble(r -> RouteCosts.totalCost(r, request))
                .max().orElse(1);

        double maxDistance = valid.stream()
                .mapToDouble(Route::distanceKm)
                .max().orElse(1);

        double maxEmission = valid.stream()
                .mapToDouble(Route::carbonEmission)
                .max().orElse(1);

        return valid.stream()
                .min(Comparator.comparingDouble(r ->
                        (maxTime == 0 ? 0 :
                                r.estimatedMinutes() / maxTime)
                                * timeWeight
                                + (maxCost == 0 ? 0 :
                                RouteCosts.totalCost(r, request) / maxCost)
                                * costWeight
                                + (maxDistance == 0 ? 0 :
                                r.distanceKm() / maxDistance)
                                * distanceWeight
                                + (maxEmission == 0 ? 0 :
                                r.carbonEmission() / maxEmission)
                                * emissionWeight
                ))
                .orElseThrow();
    }

    @Override
    public String getStrategyName() {
        return "Balanced Route";
    }
}
