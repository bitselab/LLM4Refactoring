package org.apache.flink.runtime.metrics.groups;

import org.apache.flink.annotation.Internal;
import org.apache.flink.annotation.VisibleForTesting;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.metrics.groups.OperatorIOMetricGroup;
import org.apache.flink.metrics.groups.OperatorMetricGroup;
import org.apache.flink.metrics.groups.SinkWriterMetricGroup;
import org.apache.flink.runtime.metrics.MetricNames;

/** Special {@link org.apache.flink.metrics.MetricGroup} representing an Operator. */
@Internal
public class InternalSinkWriterMetricGroup extends ProxyMetricGroup<MetricGroup>
        implements SinkWriterMetricGroup {

    private final Counter numRecordsOutErrors;
    private final Counter numRecordsSendErrors;
    private final Counter numRecordsWritten;
    private final Counter numBytesWritten;
    private final OperatorIOMetricGroup operatorIOMetricGroup;

    @VisibleForTesting
    InternalSinkWriterMetricGroup(
            MetricGroup parentMetricGroup, OperatorIOMetricGroup operatorIOMetricGroup) {
        super(parentMetricGroup);
        numRecordsOutErrors = parentMetricGroup.counter(MetricNames.NUM_RECORDS_OUT_ERRORS);
        numRecordsSendErrors =
                parentMetricGroup.counter(MetricNames.NUM_RECORDS_SEND_ERRORS, numRecordsOutErrors);
        numRecordsWritten =
                parentMetricGroup.counter(
                        MetricNames.NUM_RECORDS_SEND,
                        operatorIOMetricGroup.getNumRecordsOutCounter());
        numBytesWritten =
                parentMetricGroup.counter(
                        MetricNames.NUM_BYTES_SEND, operatorIOMetricGroup.getNumBytesOutCounter());
        this.operatorIOMetricGroup = operatorIOMetricGroup;
    }

    public static InternalSinkWriterMetricGroup wrap(OperatorMetricGroup operatorMetricGroup) {
        return new InternalSinkWriterMetricGroup(
                operatorMetricGroup, operatorMetricGroup.getIOMetricGroup());
    }
    
    @VisibleForTesting
    public static InternalSinkWriterMetricGroup mockWriterMetricGroup(MetricGroup metricGroup) {
        return new InternalSinkWriterMetricGroup(
                metricGroup, UnregisteredMetricsGroup.createOperatorIOMetricGroup());
    }

    @VisibleForTesting
    public static InternalSinkWriterMetricGroup mockWriterMetricGroup(
            MetricGroup metricGroup, OperatorIOMetricGroup operatorIOMetricGroup) {
        return new InternalSinkWriterMetricGroup(metricGroup, operatorIOMetricGroup);
    }

    @Override
    public OperatorIOMetricGroup getIOMetricGroup() {
        return operatorIOMetricGroup;
    }

    @Override
    public Counter getNumRecordsOutErrorsCounter() {
        return numRecordsOutErrors;
    }

    @Override
    public Counter getNumRecordsSendErrorsCounter() {
        return numRecordsSendErrors;
    }

    @Override
    public Counter getNumRecordsSendCounter() {
        return numRecordsWritten;
    }

    @Override
    public Counter getNumBytesSendCounter() {
        return numBytesWritten;
    }

    @Override
    public void setCurrentSendTimeGauge(Gauge<Long> currentSendTimeGauge) {
        parentMetricGroup.gauge(MetricNames.CURRENT_SEND_TIME, currentSendTimeGauge);
    }
}