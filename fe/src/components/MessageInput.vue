<script setup lang="ts">
import { computed } from "vue";

const props = defineProps<{
  modelValue?: string;
  disabled: boolean;
}>();
const emit = defineEmits<{
  (e: "update:modelValue", value: string | undefined): void;
}>();

const value = computed({
  get() {
    return props.modelValue;
  },
  set(value) {
    emit("update:modelValue", value);
  },
});
</script>

<template>
  <div class="message-input-group">
    <label class="small" for="message">Message</label>
    <input
      name="message"
      type="text"
      v-model="value"
      required="true"
      :disabled="props.disabled"
    />
  </div>
</template>

<style>
.message-input-group label {
  display: block;
}

.message-input-group input {
  background-color: #333;
  color: #ddd;
  border: 1px solid #555;
  border-radius: 4px;
  outline: none;
  padding: 5px 10px;
  width: 100%;
  font-size: 1.2em;
}
</style>
