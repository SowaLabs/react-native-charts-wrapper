//
//  PriceBalloonMarker.swift
//  BisonApp
//
//  Created by Anže Vavpetič on 18/05/2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation

import Charts

import SwiftyJSON

open class PriceBalloonMarker: BalloonMarker {
  
  fileprivate var labelns: NSMutableAttributedString?
  fileprivate var commonAttributes = [NSAttributedStringKey:Any]()
  fileprivate var boldAttributes = [NSAttributedStringKey:Any]()
  fileprivate var positiveOffsetAttributes = [NSAttributedStringKey:Any]()
  fileprivate var negativeOffsetAttributes = [NSAttributedStringKey:Any]()

  public init(color: UIColor, font: UIFont, textColor: UIColor, positiveColor: UIColor = UIColor.green, negativeColor: UIColor = UIColor.red) {
    super.init(color: color, font: font, textColor: textColor)
    
    commonAttributes[NSAttributedStringKey.font] = font
    commonAttributes[NSAttributedStringKey.paragraphStyle] = _paragraphStyle
    
    let boldDescriptor = font.fontDescriptor.addingAttributes([UIFontDescriptor.AttributeName.traits: [UIFontDescriptor.TraitKey.weight: UIFont.Weight.bold]])
    let boldFont = UIFont(descriptor: boldDescriptor, size: font.pointSize)
    boldAttributes[NSAttributedStringKey.font] = boldFont
    positiveOffsetAttributes[NSAttributedStringKey.foregroundColor] = positiveColor
    negativeOffsetAttributes[NSAttributedStringKey.foregroundColor] = negativeColor
  }
  
  public required init?(coder aDecoder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
  
  open override func refreshContent(entry: ChartDataEntry, highlight: Highlight) {
    var label : String;
    
    if let candleEntry = entry as? CandleChartDataEntry {
      
      label = candleEntry.close.description
    } else {
      label = entry.y.description
    }
    
    if let object = entry.data as? JSON {
      if object["marker"].exists() {
        let marker = object["marker"].dictionaryValue
        
        // Check for additional marker data
        if let cryptoName = marker["entity"]?.stringValue,
          let price = marker["price"]?.stringValue,
          let priceDiff = marker["priceDiff"]?.stringValue,
          let dateTime = marker["dateTime"]?.stringValue,
          let direction = marker["direction"]?.stringValue
        {
          label = "\(cryptoName) \(price)\n\(priceDiff)\n\(dateTime)"
          
          let priceRange = NSRange(location: cryptoName.count + 1, length: price.count)
          let priceDiffRange = NSRange(location: priceRange.upperBound + 1, length: priceDiff.count)
          
          labelns = NSMutableAttributedString(string: label, attributes: commonAttributes)
          labelns?.addAttributes(boldAttributes, range: priceRange)
          
          switch direction {
          case "positive":
            labelns?.addAttributes(positiveOffsetAttributes, range: priceDiffRange)
          case "negative":
            labelns?.addAttributes(negativeOffsetAttributes, range: priceDiffRange)
          default: break
          }
          
        } else {
          // otherwise
          label = object["marker"].stringValue
          labelns = NSMutableAttributedString(string: label, attributes: commonAttributes)
        }
        
        if highlight.stackIndex != -1 && object["marker"].array != nil {
          label = object["marker"].arrayValue[highlight.stackIndex].stringValue
          labelns = NSMutableAttributedString(string: label, attributes: commonAttributes)
        }
      }
    }
    
    _labelSize = labelns?.size() ?? CGSize.zero
    _size.width = _labelSize.width + self.insets.left + self.insets.right
    _size.height = _labelSize.height + self.insets.top + self.insets.bottom
    _size.width = max(minimumSize.width, _size.width)
    _size.height = max(minimumSize.height, _size.height)
  }
  
  open override func draw(context: CGContext, point: CGPoint) {
    if (labelns == nil || labelns?.length == 0) {
      return
    }
    
    context.saveGState()
    
    let rect = drawRectOnTop(context: context, point: point)
    
    UIGraphicsPushContext(context)
    
    labelns?.draw(in: rect)
    
    UIGraphicsPopContext()
    
    context.restoreGState()
  }
  
}
