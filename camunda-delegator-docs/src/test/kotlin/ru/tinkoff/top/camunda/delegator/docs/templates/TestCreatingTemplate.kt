package ru.tinkoff.top.camunda.delegator.docs.templates

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Test
import org.springframework.core.io.ClassPathResource
import ru.tinkoff.top.camunda.delegator.delegates.DelegateMetaInformation

class TestCreatingTemplate {

    val objectMapper = ObjectMapper().findAndRegisterModules().also {
        it.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    @Test
    fun testCreatingSimpleTemplate() {
        val templateService = DelegatorTemplateService()
        templateService.initTemplates(listOf(DemoDelegate()))

        val generatedTemplates = templateService.load()

        val templateFile = ClassPathResource("/delegate-templates/demoDelegateTemplate.json")
        val template = objectMapper.readTree(templateFile.url)

        Assert.assertEquals(template, objectMapper.valueToTree(generatedTemplates))
    }

    @Test
    fun testTemplateCustomizer() {
        val templateService = DelegatorTemplateService(
            listOf(object : DelegateTemplateCustomizer {
                override fun customize(
                    template: DelegateTemplate,
                    metaInformation: DelegateMetaInformation
                ): DelegateTemplate {
                    template.entriesVisible = null
                    return template
                }
            })
        )
        templateService.initTemplates(listOf(DemoDelegate()))

        val generatedTemplates = templateService.load()

        val templateFile = ClassPathResource("/delegate-templates/demoDelegateTemplateWithoutVisible.json")
        val template = objectMapper.readTree(templateFile.url)

        Assert.assertEquals(template, objectMapper.valueToTree(generatedTemplates))
    }

    @Test
    fun testAdd() {
        val templateService = DelegatorTemplateService(
            listOf(object : DelegateTemplateCustomizer {
                override fun customize(
                    template: DelegateTemplate,
                    metaInformation: DelegateMetaInformation
                ): DelegateTemplate {
                    val properties = template.properties.toMutableList()
                    properties.add(
                        Property(
                            type = PropertyType.DROPDOWN.type,
                            label = "exceptionProcessingStrategy",
                            binding = InputParameterBinding("exceptionProcessingStrategy"),
                            choices = listOf(
                                Choice("test1", "value1"),
                                Choice("test2", "value2")
                            )
                        )
                    )
                    template.properties = properties
                    return template
                }
            })
        )
        templateService.initTemplates(listOf(DemoDelegate()))

        val generatedTemplates = templateService.load()

        val templateFile = ClassPathResource("/delegate-templates/templateWithChoices.json")
        val template = objectMapper.readTree(templateFile.url)

        Assert.assertEquals(template, objectMapper.valueToTree(generatedTemplates))
    }
}
