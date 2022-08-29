<script setup lang="ts">import { CalloutType } from '@/types/types';
import { computed } from 'vue';

const props = defineProps<{
    type: CalloutType,
}>();

const extraClasses = computed(() => {
    switch (props.type) {
        case CalloutType.ERROR:
            return ["callout-error"];
        case CalloutType.SUCCESS:
            return ["callout-success"];
    }
});
</script>

<template>
    <div :class="['callout', ...extraClasses]">
        <slot></slot>
    </div>
</template>

<style>
    .callout {
        padding: 0.8em;
        border-width: 1px;
        border-style: solid;
        font-size: 0.8em;
    }

    .callout-error {
        background-color: #440303;
        border-color: #971c1c;
        color: #fff;
    }

    .callout-success {
        background-color: #034403;
        border-color: #1c971c;
        color: #fff;
    }
</style>
