package com.example.courseservice.controllers.redirect;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.courseservice.data.dto.response.TransactionResponse;
import com.example.courseservice.services.transactionservice.TransactionService;
import com.example.courseservice.utils.EnvironmentVariable;

@Controller
public class RedirectController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private EnvironmentVariable environmentVariable;


    @GetMapping("/check-payment")
    @ResponseBody
    public String transactionPage(@RequestParam(value = "vnp_TxnRef") String vnpTxnRef,
        @RequestParam(value = "vnp_BankTranNo") String transactionNo,
        @RequestParam(value = "vnp_PayDate") String transDate,
        @RequestParam(value = "vnp_ResponseCode") String responseCode,
        HttpSession session) throws Exception {
            TransactionResponse transactionResponse = transactionService.checkPaymentStatus(vnpTxnRef, responseCode, transactionNo, transDate);
        session.setAttribute("transaction_status", transactionResponse.getTransactionStatus().toString());
        return "redirect:"+environmentVariable.getVnPayRedirectUrl();
    }
}
