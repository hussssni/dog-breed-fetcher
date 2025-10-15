package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached.
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher delegate;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.delegate = Objects.requireNonNull(fetcher, "fetcher");
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String key = (breed == null) ? "" : breed.toLowerCase(Locale.ROOT);
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        // Miss ⇒ call underlying fetcher (count it), and cache only on success.
        callsMade++;
        List<String> result = delegate.getSubBreeds(breed);
        List<String> copy = Collections.unmodifiableList(new ArrayList<>(result));
        cache.put(key, copy);
        return copy;
    }

    public int getCallsMade() {
        return callsMade;
    }
}
