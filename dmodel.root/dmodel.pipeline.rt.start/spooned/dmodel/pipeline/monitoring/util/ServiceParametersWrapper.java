package dmodel.pipeline.monitoring.util;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_TRAILING_COMMA;


public class ServiceParametersWrapper {
    /**
     * Service parameters instance having no parameters set. This instance is shared
     * to reduce memory consumption.
     */
    public static ServiceParametersWrapper EMPTY = new ServiceParametersWrapper();

    private static final TypeReference<TreeMap<String, Object>> PARSED_PARAMETERS_TYPE_REF = new TypeReference<TreeMap<String, Object>>() {};

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        ServiceParametersWrapper.mapper.enable(ALLOW_TRAILING_COMMA);
    }

    private final SortedMap<String, Object> parameters;

    private final SortedMap<String, Object> readOnlyParameters;

    private ServiceParametersWrapper() {
        this.parameters = Collections.emptySortedMap();
        this.readOnlyParameters = Collections.unmodifiableSortedMap(this.parameters);
    }

    private ServiceParametersWrapper(final Map<String, Object> parameters) {
        this.parameters = new TreeMap<>(parameters);
        this.readOnlyParameters = Collections.unmodifiableSortedMap(this.parameters);
    }

    private ServiceParametersWrapper(final String parameters) throws IOException {
        this.parameters = ServiceParametersWrapper.mapper.readValue(parameters, ServiceParametersWrapper.PARSED_PARAMETERS_TYPE_REF);
        this.readOnlyParameters = Collections.unmodifiableSortedMap(this.parameters);
    }

    @Override
    public boolean equals(final Object obj) {
        if ((this) == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if ((this.getClass()) != (obj.getClass())) {
            return false;
        }
        ServiceParametersWrapper other = ((ServiceParametersWrapper) (obj));
        return this.parameters.equals(other.parameters);
    }

    /**
     * Gets the service parameters ordered by name.
     *
     * @return The service parameters.
     */
    public SortedMap<String, Object> getParameters() {
        return this.parameters;
    }

    @Override
    public int hashCode() {
        return this.parameters.hashCode();
    }

    /**
     * Returns an instance of {@link ServiceParametersWrapper}, containing the
     * passed parameters.
     *
     * @param parameters
     * 		The service parameters.
     * @return An instance of {@link ServiceParametersWrapper}, containing the
    passed parameters.
     */
    public static ServiceParametersWrapper build(final Map<String, Object> parameters) {
        if ((parameters == null) || (parameters.isEmpty())) {
            return ServiceParametersWrapper.EMPTY;
        }
        return new ServiceParametersWrapper(parameters);
    }

    /**
     * Returns an instance of {@link ServiceParametersWrapper}, containing the
     * passed parameters.
     *
     * @param parameters
     * 		The parameters in json format.
     * @return An instance of {@link ServiceParametersWrapper}, containing the
    passed parameters.
     */
    public static ServiceParametersWrapper buildFromJson(final String parameters) {
        if ((parameters == null) || (parameters.isEmpty())) {
            return ServiceParametersWrapper.EMPTY;
        }
        try {
            return new ServiceParametersWrapper(parameters);
        } catch (IOException e) {
            return ServiceParametersWrapper.EMPTY;
        }
    }
}

