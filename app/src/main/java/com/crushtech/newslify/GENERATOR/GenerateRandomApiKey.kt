package com.crushtech.newslify.GENERATOR

import kotlin.random.Random

class GenerateRandomApiKey {
    companion object {
        val list = listOf(
            "a0b90969cd63469b81164236f674b2cd",
            "37cade18ac8b43178465a5e8eb3337f9",
            "0c377f4551184aaca432bf4fefc94596",
            "0c377f4551184aaca432bf4fefc94596",
            "a49d820b710149bab342da187bee15c0",
            "12796f76ac814612bb58f572bfc4a5a0",
            "5fcb5146577046a18786bb167c63e1e9",
            "eef0919258984c6fa5e513250aea3c1e",
            "df8fab08721f40438c234de2b23ffb76",
            "94f84f88af9d464da16766c9fb4a9f69",
            "91fa063c8d8643e0a0aed7a4e5ddc49b",
            "40ddedebd93d420e982d46168aa60f2f",
            "060bd14f65964662808376cb148e4e0f",
            "682f4735a1e34da1b40bc204013cced9",
            "5aa09f4d3b2e444e979551e4c8740838",
            "0fe60cfb25c94799a88bbdc34741e90c",
            "c9a44f67a30b40129ac81be8e22697e0",
            "e9b93d9aae624775b8cc5c94c5fa2c84",
            "8c7e1f754a004b0eb9c20a41980f5f1f",
            "32d5bdac2f94449b977a7a439d7c4beb",
            "415ca1c3a02444859fe53dbddb677504",
            "74380ecaf7e04bcbb3756564ed69cbf5",
            "2e0af52e567e40c8b847b6709738317b",
            "dc13d88f2c6b4a89831365b25dfa4b39",
            "28ed3f34899840e69000610ce22b47f3",
            "b099d1ef26ed486cacb548108eb0a722",
            "daf5777183a0450188b29c021eec8d03",
            "998f711a08064bf7a11cf8b640be5a37",
            "b06ee42e8ac1483daf22d3dcb5a0d537",
            "84943ef5bbb74bd2a334a43d7cf151fc",
            "33ad2a3f50eb4ecb8251bca5ee10530c",
            "be775f2a5c734cc98076ccfccbc32f2b",
            "6e92148ad9274574aeaa1fc6070cc028",
            "12dbe1f1ae9844c787d3a13bad0d2cbf",
            "4be414c849cb46fd80609ab156b4fe32",
            "6212d002d6ab4235af4b29bf416997dc",
            "db4bd84671bf47f7bc0d895e37c7797c",
            "9f03795d1f4b482892ad50f32231a5af",
            "f0fbc1e89d154be389862d526c0edc2d",
            "ef218d0c9cca4cc0b0b2ac18612b08c5",
            "e3c309e17096411cab00ec7c9b20667c",
            "e98384f67e4b452cba9d53911b5c37c6",
            "9b92e2d0a3e84af2964b0bf785737bea",
            "cfb0a7dc078347ebb75261eae8e50d8a",
            "5679249751b9443191d4257d367167f4",
            "a85c6d554e6e4fe7af1cfaf1bb735f58",
            "5fa6a2bef57043c48cb56eaa91698b50",
            "5d8e17c06ad94161b76a2700d6e882a7",
            "67242c53238a419a9f4de601ba60d564",
            "d265e4cbc4ce45e49aebd6628607c2ac",
            "68f1761e2a904963bdfe2cd9abc7d95f",
            "7a5676d092014f529842ca00d68b8ca8",
            "ca4629b4e77d4e8baa38cdaed8e02dd7",
            "f16a00f957bc452ba79e2b167e00d6f3",
            "e1403b2433cf492e8b7c941ece28be6a",
            "56dbd0ca61804d869d455e1f08c7ce7a",
            "db322f5d5b654dbd9040b15ecd932e69",
            "ac0b425485bf4159848945ccf990057e"

        )
        private val randomValue = Random.nextInt(list.size)
        val API_KEY = list[randomValue]

    }
}
