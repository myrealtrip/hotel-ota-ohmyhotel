package com.myrealtrip.ohmyhotel.batch.tasklet;

import com.myrealtrip.ohmyhotel.core.domain.partner.dto.Partner;
import com.myrealtrip.ohmyhotel.core.provider.partner.PartnerProvider;
import com.myrealtrip.ohmyhotel.enumarate.PartnerCommissionType;
import com.myrealtrip.ohmyhotel.outbound.agent.mrt.settle.SettleConfigAgent;
import com.myrealtrip.settle.web.values.SaleCommissionPolicyInquiryResponse;
import com.myrealtrip.settle.web.values.SettlementConfigResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class PartnerSettleInfoSyncTasklet implements Tasklet {

    private final String partnerId;
    private final PartnerProvider partnerProvider;
    private final SettleConfigAgent settleConfigAgent;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Partner oldPartner = partnerProvider.getByPartnerId(Long.valueOf(partnerId));
        SaleCommissionPolicyInquiryResponse settlementConfigResponse = settleConfigAgent.getSettlementConfig(partnerId);
        Partner newPartner = toNewPartner(oldPartner, settlementConfigResponse);
        partnerProvider.upsert(newPartner);
        return RepeatStatus.FINISHED;
    }

    public Partner toNewPartner(Partner oldPartner, SaleCommissionPolicyInquiryResponse settlementConfigResponse) {
        if (isNull(oldPartner)) {
            return Partner.builder()
                .partnerId(Long.valueOf(partnerId))
                .commissionType(EnumUtils.getEnum(PartnerCommissionType.class, settlementConfigResponse.getCommissionCalculationType().name()))
                .commissionRate(settlementConfigResponse.getCommissionRate())
                .saleCommissionPolicyId(settlementConfigResponse.getSaleCommissionPolicyId())
                .build();
        }
        return oldPartner.toBuilder()
            .commissionType(EnumUtils.getEnum(PartnerCommissionType.class, settlementConfigResponse.getCommissionCalculationType().name()))
            .commissionRate(settlementConfigResponse.getCommissionRate())
            .saleCommissionPolicyId(settlementConfigResponse.getSaleCommissionPolicyId())
            .build();
    }
}
